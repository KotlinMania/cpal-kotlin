# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 5/32 (15.6%)
- **Function parity:** 43/228 matched (target 97) — 18.9%
- **Class/type parity:** 31/89 matched (target 65) — 34.8%
- **Combined symbol parity:** 74/317 matched (target 162) — 23.3%
- **Average inline-code cosine:** 0.32 (function body across 4 matched files)
- **Average documentation cosine:** 0.79 (doc text across 4 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 5 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `cpal.Lib`
- **Similarity:** 0.54
- **Dependents:** 0
- **Priority Score:** 145104.6
- **Functions:** 21/33 matched (target 45)
- **Missing functions:** `mul`, `describe`, `into_abi`, `as_nanos`, `from_nanos`, `from_nanos_i128`, `from_secs_f64`, `from_parts`, `bytes_mut`, `as_slice`, `as_slice_mut`, `from`
- **Types:** 16/18 matched (target 21)
- **Missing types:** `Output`, `Abi`
- **Tests:** 2/2 matched

### 2. null.mod

- **Target:** `nullhost.Null [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 82210.0
- **Functions:** 14/15 matched (target 22)
- **Missing functions:** `next`
- **Types:** 0/7 matched (target 4)
- **Missing types:** `Devices`, `Device`, `Host`, `Stream`, `SupportedInputConfigs`, `SupportedOutputConfigs`, `Item`

### 3. error

- **Target:** `cpal.Error`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 21210.0
- **Functions:** 0/2 matched (target 11)
- **Missing functions:** `fmt`, `from`
- **Types:** 10/10 matched (target 16)
- **Missing types:** _none_

### 4. traits

- **Target:** `cpal.Traits`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 20905.3
- **Functions:** 4/6 matched (target 4)
- **Missing functions:** `supports_input`, `supports_output`
- **Types:** 3/3 matched (target 6)
- **Missing types:** _none_

### 5. samples_formats

- **Target:** `cpal.SamplesFormats`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 10707.3
- **Functions:** 4/5 matched (target 15)
- **Missing functions:** `fmt`
- **Types:** 2/2 matched (target 18)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `alsa.mod` | `host.alsa.Mod` | 0 | `host/alsa/mod.rs` | `host/alsa/Mod.kt` |
| `asio.mod` | `host.asio.Mod` | 0 | `host/asio/mod.rs` | `host/asio/Mod.kt` |
| `ios.mod` | `host.coreaudio.ios.Mod` | 0 | `host/coreaudio/ios/mod.rs` | `host/coreaudio/ios/Mod.kt` |
| `macos.mod` | `host.coreaudio.macos.Mod` | 0 | `host/coreaudio/macos/mod.rs` | `host/coreaudio/macos/Mod.kt` |
| `coreaudio.mod` | `host.coreaudio.Mod` | 0 | `host/coreaudio/mod.rs` | `host/coreaudio/Mod.kt` |
| `emscripten.mod` | `host.emscripten.Mod` | 0 | `host/emscripten/mod.rs` | `host/emscripten/Mod.kt` |
| `jack.mod` | `host.jack.Mod` | 0 | `host/jack/mod.rs` | `host/jack/Mod.kt` |
| `host.mod` | `host.Mod` | 0 | `host/mod.rs` | `host/Mod.kt` |
| `oboe.mod` | `host.oboe.Mod` | 0 | `host/oboe/mod.rs` | `host/oboe/Mod.kt` |
| `wasapi.mod` | `host.wasapi.Mod` | 0 | `host/wasapi/mod.rs` | `host/wasapi/Mod.kt` |
| `webaudio.mod` | `host.webaudio.Mod` | 0 | `host/webaudio/mod.rs` | `host/webaudio/Mod.kt` |
| `platform.mod` | `platform.Mod` | 0 | `platform/mod.rs` | `platform/Mod.kt` |

