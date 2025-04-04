# Run with uvicorn app:app --host 0.0.0.0 --port 8000 --reload
from fastapi import FastAPI
from pydantic import BaseModel
import torch
from transformers import RobertaForSequenceClassification, AutoTokenizer

app = FastAPI()

# Load model and tokenizer
model = RobertaForSequenceClassification.from_pretrained("wonrax/phobert-base-vietnamese-sentiment")
tokenizer = AutoTokenizer.from_pretrained("wonrax/phobert-base-vietnamese-sentiment", use_fast=False)

class TextInput(BaseModel):
    text: str

@app.post("/predict/")
async def predict(input_text: TextInput):
    sentence = input_text.text
    input_ids = torch.tensor([tokenizer.encode(sentence)])

    with torch.no_grad():
        out = model(input_ids)
        scores = out.logits.softmax(dim=-1).tolist()[0]
    
    labels = ["NEG", "POS", "NEU"]
    result = {labels[i]: scores[i] for i in range(len(labels))}
    
    return {"sentiment": labels[scores.index(max(scores))], "scores": result}
