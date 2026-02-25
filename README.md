# Ai

This repository is an experimental skeleton for running an open-source large
language model locally on Android (API 14+) and integrating with Termux.  It
is **not** a drop-in replacement for ChatGPT; rather it shows the pieces you
need to assemble if you want to download, package, and execute a quantized
LLM on-device.

The goals:

1. Obtain a lightweight, quantized open-source model (e.g. LLaMA‑2 7B, GPT‑4‑all,
   Mistral, etc.) and store it in `models/`.
2. Provide an Android example app that loads the model with a mobile inference
   engine (TensorFlow Lite, ONNX Runtime, or llama.cpp via JNI) and presents a
   simple chat UI.
3. Allow command‑line access in Termux so that you can run the same model with a
   text interface and scriptable I/O.
4. Document how to build an APK locally and how to generate your own APKs by
   editing the Kotlin/Java source.

> ⚠️ Running anything resembling a "best" model on a phone requires aggressive
> quantisation; realistic workflows use 4‑bit or 8‑bit GGML models and depend on
> the device's CPU/RAM.  You will not get GPT‑4 quality with these constraints.

---

## Structure

- `android/` – contains a minimal Android Studio project (Kotlin, Compose) with
  placeholder UI (including a language selector) and a spot to load a local
  model from `assets/` or `externalStorage`.
- `scripts/` – helper utilities, e.g. `setup_model.py` for downloading a model
  from Hugging Face.
- `termux/` – examples showing how to run the model in Termux with a simple
  Python CLI.
- `models/` – the place to put downloaded/converted `.ggml`/`.tflite` files.

## Getting started

1. **Download a model** – run `python3 scripts/setup_model.py` on your desktop
   (requires `huggingface_hub` and an HF token).  Convert the result using
   `llama.cpp` (`./main -m pytorch_model.bin --conv`).  Copy the final
   `model.ggml` into `models/`.

2. **Prepare Android assets** – copy the GGML or TFLite file into
   `android/app/src/main/assets/`.  Update `MainActivity.kt` to invoke your
   inference engine library (JNI wrapper for llama.cpp, or TensorFlow Lite
   automatically if you're using a `.tflite` file).

3. **Build the APK** – open the `android/` directory in Android Studio or run
   from the command line:
   ```sh
   cd android
   ./gradlew assembleDebug
   ```
   The result `app-debug.apk` will be under `android/app/build/outputs/apk`.

   You can also have the AI generate arbitrary archives using provided
   helper scripts.  For example, `scripts/generate_package.sh` will create a
   ZIP (and a dummy APK) from files written by a prompt-driven process:
   ```sh
   chmod +x scripts/generate_package.sh
   ./scripts/generate_package.sh
   ```
   The script is just an illustration – you could replace the body with a
   call to your model executable and let it `echo` file contents before
   packaging them.

4. **Install on a device** – use `adb install -r android/app/build/outputs/apk/debug/app-debug.apk`
   or copy to your phone and install manually.

5. **Termux usage** – transfer the `model.ggml` file and the compiled
   `llama.cpp` `main` binary to your Termux home.  Launch `python3 chat_cli.py`.

6. **Making your own APKs quickly** – the Android project is normal Gradle
   code; modifying the UI and behaviour and running `./gradlew assembleDebug`
   produces a new APK using the same model assets.

7. **Conversation memory** – the sample app now remembers every exchange with
   the AI.  Messages are kept in a simple history list and persisted in
   `SharedPreferences` as JSON.  When the app restarts the previous conversation
   is reloaded, just like ChatGPT’s chat history.  You can clear or export this
   history by editing the persistence functions in `MainActivity.kt`.


## Extending the project

- Replace the placeholder `MainActivity` UI with something richer, connect to
  the model at runtime using whichever inference engine you prefer.
- The UI now includes a file picker and settings screen; you can use the
  picker to feed arbitrary files (text, zip, apk, images, audio, etc.) to your
  model or have the model output generated files to disk.  Handling of every
  format is up to your inference code – the picker just returns a URI and the
  model can ingest the raw bytes or metadata as needed.
  The language field is now free-text so you can type the name of *any* natural
  or programming language; the AI interprets it as a hint, but otherwise the
  model itself determines how to handle the input.
- Added a button and placeholder logic for image generation.  You can integrate
  any offline image-generation model (Stable Diffusion, Mistral Vision, etc.)
  and display the returned bitmap in the UI.  The example code shows where to
  hook this up.
- **Feature/plugin framework** – the app is built to accommodate the
  *maximum* number of capabilities.  A `Feature` interface and `FeatureRegistry`
  let you register independent modules (translation, voice, calculator,
  browser, code executor, etc.).  The settings screen lists enabled features,
  and you can add new ones simply by implementing the interface and calling
  `FeatureRegistry.register(...)` from anywhere.  This is the basis for having
  "more functions than any other AI" – each plugin can expose whatever it
  needs.
- Add personalisation options (theme, username, behaviour) in
  `SettingsActivity.kt` and persist them in `SharedPreferences` or a
  database.
- Use JNI (e.g. via NDK) to compile `llama.cpp` for ARM32/ARM64 and call it from
  Kotlin; see [llama.cpp README](https://github.com/ggerganov/llama.cpp) for
  cross‑compilation hints.
- Give the AI “a world” by creating a directory structure or ZIP archive from
  prompts; you could script generation of a `.zip` or even a raw `.apk` rather
  than rely on the Android build system.  We've included a `scripts/` directory
  where you can put packaging helpers (e.g. a `generate_package.sh` that
  creates a ZIP from template files using input from the model).
- Add support for other models or external storage on the device.
- Automate conversion of models to TFLite/ONNX if you want to use
  TensorFlow Lite or ONNX Runtime Mobile instead of llama.cpp.

Enjoy hacking, but remember that workable performance depends on choosing a
small model and keeping everything local.

### 🛠️ Potential features to outrun other AI

Below is a non-exhaustive list of ideas you can implement with the plugin
system.  The goal is to let this app accumulate *more functions than any
other* open‑source assistant:

- **Natural‑language tasks**: summarization, translation, Q&A, sentiment analysis.
- **Code features**: syntax highlighting, compilation, execution sandbox, error
  explanation, code search, multi‑language support.
- **File operations**: open/preview any format, edit documents, diff directories,
  archive creation, APK inspector, zip/unzip, media transcoding.
- **Image and multimedia**: on‑device image generation, editing, OCR, captioning,
  video frame synthesis, audio transcription/synthesis.
- **Voice & speech**: speech‑to‑text, text‑to‑speech, wake‑word listener.
- **Connectivity**: web browser window, RSS reader, email client, RSS-writer,
  offline caching, peer‑to‑peer chat.
- **Utilities**: calculator, calendar, reminders, unit converter, weather,
  maps, file manager.
- **System hooks**: Termux integration, shell execution, Git client, package
  manager wrapper, system status dashboard.
- **Customization**: themes, keyboard shortcuts, macros, user profiles,
  plugin marketplace.
- **Persistence**: conversation history, notes, task lists, local database,
  cloud sync (optional).
- **Meta‑AI**: allow the model to modify its own prompt templates, update
  weights (fine‑tuning), or spawn sub‑agents that tackle subtasks.

Because each of these can be a self‑contained feature class implementing
`Feature`, the app can grow organically.  Ship all plugins you build, or
provide a directory where users drop new `.jar`/`.dex` plugins that the loader
discovers at startup.

Feel free to copy this list back into your own notes or add more – the
framework doesn’t care how many features you plug in.
