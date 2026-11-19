"""Smoke test: embed sample sentences and store them in the rag schema.

Inserts one test repo, one file, one chunk per sentence and one embedding per
chunk. Every run adds new rows. Nothing is deleted.
"""

import logging

import psycopg2
from psycopg2.extras import execute_values
from sentence_transformers import SentenceTransformer

logging.basicConfig(level=logging.INFO, format="%(levelname)s %(message)s")
log = logging.getLogger(__name__)

MODEL_NAME = "all-MiniLM-L6-v2"  # 384 dims, matches vector(384)
# Earlier runs already stored: "Cats drink milk and eat fish", "A cat is a pet animal",
# "Put petrol in the car", "The engine is overheating".
# This made-up fact exists only in our DB, so an answer that uses it proves the LLM read the context.
SENTENCES = [
    "My cat Sheru only eats paneer tikka.",
]


def main() -> None:
    model = SentenceTransformer(MODEL_NAME)
    vectors = model.encode(SENTENCES)

    conn = psycopg2.connect(host="localhost", database="rag_assistant", user="dhaval")
    try:
        # "with conn" commits on success and rolls back on any error.
        with conn, conn.cursor() as cursor:
            cursor.execute(
                "INSERT INTO rag.repo (git_url, status) VALUES (%s, %s) "
                "RETURNING repo_id",
                ("https://example.com/test.git", "TEST"),
            )
            repo_id = cursor.fetchone()[0]

            cursor.execute(
                "INSERT INTO rag.files (repo_id, file_path, extension) "
                "VALUES (%s, %s, %s) RETURNING file_id",
                (repo_id, "test/sample.txt", "txt"),
            )
            file_id = cursor.fetchone()[0]

            # One INSERT statement for all chunks.
            chunk_rows = execute_values(
                cursor,
                "INSERT INTO rag.chunks (file_id, chunk_text, chunk_index) "
                "VALUES %s RETURNING chunk_index, chunk_id",
                [(file_id, text, index) for index, text in enumerate(SENTENCES)],
                fetch=True,
            )
            chunk_ids = {index: chunk_id for index, chunk_id in chunk_rows}

            # One INSERT statement for all embeddings.
            execute_values(
                cursor,
                "INSERT INTO rag.embedding (chunk_id, embedding) VALUES %s",
                [
                    (chunk_ids[index], vector.tolist())
                    for index, vector in enumerate(vectors)
                ],
                template="(%s, %s::vector)",
            )

            cursor.execute(
                "SELECT c.chunk_index, c.chunk_text, vector_dims(e.embedding) "
                "FROM rag.embedding e "
                "JOIN rag.chunks c ON c.chunk_id = e.chunk_id "
                "WHERE c.file_id = %s ORDER BY c.chunk_index",
                (file_id,),
            )
            for chunk_index, chunk_text, dims in cursor.fetchall():
                log.info("chunk %d | dims=%d | %s", chunk_index, dims, chunk_text)
    finally:
        conn.close()


if __name__ == "__main__":
    main()