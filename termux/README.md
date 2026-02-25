# Termux Integration

This folder contains a lightweight Python CLI that uses `llama.cpp` binaries
and a local GGML model file. Copy the `model.ggml` you prepared on your desktop
into this directory on your Android device (via `adb push` or similar) and run:

```sh
pkg install python git clang make
git clone https://github.com/ggerganov/llama.cpp
cd llama.cpp
make
# copy the compiled `main` binary back to the termux folder
```

Then start the chat:

```sh
python3 chat_cli.py
```

Termux also supports the `termux-api` package; you can write scripts that
interact with Android features (notifications, clipboard, etc.) and feed the
results into the chat client.  The CLI above is a starting point; consider
adding a shell wrapper or alias that builds the command line for you.
