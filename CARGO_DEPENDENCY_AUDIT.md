# Cargo Dependency Audit

This audit is based on `tmp/cpal/Cargo.toml` and `tmp/cpal/Cargo.toml.orig`.

## Kotlinmania Sibling Ports

| Cargo package | Kotlinmania repo | Gradle status |
|---|---|---|
| `anyhow` | `anyhow-kotlin` | `io.github.kotlinmania:anyhow-kotlin:0.1.0` is published, but it has no JVM variant, so it cannot be used by this repo's current `jvmTest` target. |
| `clap` | `clap-kotlin` | Repo exists, but `io.github.kotlinmania:clap-kotlin` has no published Maven metadata. |
| `core-foundation-sys` | `core-foundation-kotlin` | Repo exists, but `io.github.kotlinmania:core-foundation-kotlin` has no published Maven metadata. |
| `libc` | `libc-kotlin` | Repo exists, but `io.github.kotlinmania:libc-kotlin` has no published Maven metadata. |
| `windows` | `windows-kotlin` | Repo exists, but `io.github.kotlinmania:windows-kotlin` has no published Maven metadata. |

The unpublished siblings must not be added as ordinary Gradle coordinates yet; doing so fails
metadata resolution before the ported sources can compile. Wire them in as soon as their published
coordinates exist, or introduce a deliberate composite-build policy for kotlinmania ports.

## Missing Kotlinmania Ports

The CPAL Cargo manifest also depends on crates that have no conventional sibling port in this
workspace:

| Cargo package | Use |
|---|---|
| `dasp_sample` | Public sample traits and sample-width types re-exported by CPAL. |
| `hound` | Example/test WAV writing. |
| `ringbuf` | Example/test buffering. |
| `alsa` | Linux and BSD backend. |
| `jack` | Optional JACK backend. |
| `asio-sys` | Optional Windows ASIO backend. |
| `num-traits` | Optional Windows ASIO numeric conversion. |
| `mach2` | Apple backend timebase access. |
| `coreaudio-rs` | Apple backend audio APIs. |
| `oboe` | Android backend. |
| `ndk` | Android backend. |
| `ndk-context` | Android backend. |
| `jni` | Android backend. |
| `ndk-glue` | Android example support. |
| `wasm-bindgen` | Web and Emscripten backends. |
| `wasm-bindgen-futures` | Emscripten backend. |
| `js-sys` | Web and Emscripten backends. |
| `web-sys` | Web and Emscripten backends. |
