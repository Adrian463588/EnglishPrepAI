# Implementation Plan: Offline AI, SAF Import & Speaking/Writing Coaching

**Branch**: `003-offline-ai-saf-gemma4-whisper` | **Date**: 2026-11-16 | **Spec**: `specs/003-offline-ai-saf-gemma4-whisper/spec.md`

## Technical Context
- **LLM Runtime**: `com.google.ai.edge.litertlm:litertlm-android:0.13.1` (`Engine`, `EngineConfig`, `Backend.CPU`/`GPU`, `Conversation`, `MessageCallback`, `cancelProcess()`)
- **STT Runtime**: `com.microsoft.onnxruntime:onnxruntime-android:1.22.0` (`OrtEnvironment`, `OrtSession`) + pure-Kotlin 80-mel Whisper STFT feature extractor + Whisper byte-level BPE decoder
- **Audio Capture**: `android.media.AudioRecord` (16 kHz, 16-bit mono PCM -> local WAV in `files/recordings/`) + `android.speech.tts.TextToSpeech` helper
- **SAF Security**: `ContentResolver.takePersistableUriPermission`, `DocumentFile`, SHA-256 streaming digest, zip-slip + decompression ratio guards, `StatFs` storage check, `ActivityManager.MemoryInfo` RAM check
- **UI**: `ui/feature/chat/ChatScreen.kt`, Speaking/Writing components inside `ExamScreen.kt`, Offline AI manager in `ui/feature/profile/ProfileScreen.kt`
