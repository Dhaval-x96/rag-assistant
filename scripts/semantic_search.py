
import psycopg2
from sentence_transformers import SentenceTransformer


QUESTION = "What do cats eat?"

MODEL_NAME = "all-MiniLM-L6-v2"

# Load model and encode first, so the DB connection is not left idle.
model = SentenceTransformer(MODEL_NAME)
vector = model.encode(QUESTION)  # shape (384,)

print("Connecting to the database...")
conn = psycopg2.connect(host="localhost", database="rag_assistant", user="dhaval")

try:
    with conn, conn.cursor() as cursor:
        cursor.execute(
            "SELECT c.chunk_index, c.chunk_text, e.embedding <=> %s::vector AS distance "
            "FROM rag.embedding e "
            "JOIN rag.chunks c ON c.chunk_id = e.chunk_id "
            "ORDER BY distance "
            "LIMIT 5",
            (vector.tolist(),),
        )

        for chunk_index, chunk_text, distance in cursor.fetchall():
            print(f"chunk {chunk_index} | distance={distance} | {chunk_text}")
finally:
    conn.close()