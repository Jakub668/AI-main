"""Simple helper to download a pre-trained open-source LLM and convert it for mobile use.

This script uses the `huggingface_hub` library to fetch a quantized
model (e.g. LLaMA 2 7B) and then calls `llama.cpp` to convert it to a
portable GGML file.  The output can be placed in `android/app/src/main/assets`
for shipping with an APK or used from Termux.

In practice you'll need to have your HF token available and `llama.cpp`
installed locally.  On Android/Termux you compile llama.cpp via:

    git clone https://github.com/ggerganov/llama.cpp
    cd llama.cpp
    make

Then run this script from a regular Linux machine to pre‑download the
model; the binary will be copied into `models/`.
"""
import os
import sys

from huggingface_hub import hf_hub_download

MODEL_NAME = "meta-llama/Llama-2-7b"
OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "..", "models")


def download():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    print(f"Downloading model {MODEL_NAME} ...")
    try:
        path = hf_hub_download(repo_id=MODEL_NAME, filename="pytorch_model.bin")
        print("Model downloaded to", path)
    except Exception as e:
        print("error downloading model", e)
        sys.exit(1)

    # conversion step placeholder
    print("You will still need to convert the file with llama.cpp/GGML.")


if __name__ == "__main__":
    download()
