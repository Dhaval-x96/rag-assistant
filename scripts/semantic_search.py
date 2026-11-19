
import psycopg2
from sentence_transformers import SentenceTransformer
import requests
import os

# Test questions:
#   "What does Sheru eat?"      -> expect "paneer tikka" (fact exists only in our DB)
#   "How do I bake a cake?"     -> expect "I don't know" (nothing in the DB about it)
QUESTION = "What does Sheru eat?"
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
    
        ## We can use this top most chunk text and question and pass
        ## to llm for give proper ans.
        chunks =[]
        for chunk_index, chunk_text, distance in cursor.fetchall():
            print(f"chunk {chunk_index} | distance={distance} | {chunk_text}")
            chunks.append(chunk_text)

        context = " ".join(chunks)
        token = os.getenv("ANTHROPIC_API_KEY")
        if not token:
            raise ValueError("ANTHROPIC_API_KEY environment variable is not set.")
        
        response = requests.post("https://api.anthropic.com/v1/messages", 
                                 headers={
            "Content-Type": "application/json",
            "x-api-key": token,
            "Anthropic-Version": "2026-10-21"
        },
        json={
            "model": "claude-haiku-5-5",
            "messages": [
                {"role": "user", "content": f"Answer the question based only on the context below. If the answer is not in the context, say you don't know.\n\nContext: {context}\n\nQuestion: {QUESTION}"},
            ],
            "max_tokens": 1024,
            "system": "You are a helpful assistant."
        })

        # content can also hold "thinking" blocks, so print only the text blocks.
        for block in response.json()["content"]:
            if block["type"] == "text":
                print(block["text"])

finally:
    conn.close()