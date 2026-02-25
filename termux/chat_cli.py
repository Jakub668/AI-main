#!/usr/bin/env python3
"""Simple command-line chat client for a local LLM using llama.cpp.

Intended to run in Termux on Android, assumes `./main` from llama.cpp has
been built and a model file `model.ggml` is placed alongside the script.
"""
import os
import subprocess
import sys

MODEL_PATH = "./model.ggml"

if not os.path.exists(MODEL_PATH):
    print("Place a ggml model file named model.ggml in this directory.")
    sys.exit(1)

print("Entering chat. Type 'exit' to quit.")
while True:
    prompt = input("You: ")
    if prompt.strip().lower() in ("exit", "quit"):
        break
    # call llama.cpp executable with prompt
    result = subprocess.run(["./main", "-m", MODEL_PATH, "-p", prompt], capture_output=True, text=True)
    print(result.stdout)
