# Feature Specification: Offline AI, SAF Import (Gemma 4 & Whisper ONNX) & Speaking/Writing Coaching

**Feature Branch**: `003-offline-ai-saf-gemma4-whisper`
**Created**: 2026-11-16
**Status**: Approved for Implementation

## Invariants
- Objective quizzes, audio playback, and progress analytics NEVER depend on an installed LLM or STT model.
- Gemma 4 support uses `com.google.ai.edge.litertlm:litertlm-android:0.13.1` with validated `.litertlm` artifacts ONLY (never relabeled Gemma 3 binaries).
- Whisper ONNX uses `com.microsoft.onnxruntime:onnxruntime-android:1.22.0` (`SpeechToTextEngine`) with 16 kHz mono PCM WAV/float audio captured locally via `AudioRecord`; incompatible/absent models degrade gracefully to self-review / deterministic checklist coaching.
- SAF import MUST validate file extension/MIME, header/magic bytes, SHA-256 checksum, free disk space, ABI (`arm64-v8a` / `x86_64`), and estimated RAM before atomically copying to app-private `files/models/<model-id>/`. Revoked permissions never crash the app.
- RAG AI tutor prompts MUST isolate user/imported study text from system directives (prompt-injection defense) and cite local `CourseModule` / `Passage` source IDs.

## User Scenarios & Testing

### User Story 1 - SAF Model & Study Pack Import (Priority: P1)
User downloads Gemma 4 (`.litertlm` e.g. `litert-community/gemma-4-E2B-it-litert-lm`) or Whisper ONNX (`onnx-community/whisper-tiny.en` / `csukuangfj/sherpa-onnx-whisper-tiny.en`) externally via browser/Hugging Face, then selects the file or folder in **Profile -> Offline AI -> Import**.

**Acceptance Scenarios**:
1. **Given** user picks a valid `.litertlm` file or Whisper ONNX folder via SAF, **When** pre-flight validator checks header, size, disk space, and SHA-256, **Then** file copies atomically into `files/models/`, registers in `ModelInstall`, and becomes active with zero cloud calls.
2. **Given** user picks a 0-byte file, wrong architecture/format, insufficient disk space, or revokes SAF permission later, **When** validation or load runs, **Then** app displays exact cause and recovery/re-import CTA without crash or deleting existing working models.
3. **Given** user imports a `.zip` study pack containing `../` traversal or oversized uncompressed stream, **When** `SafPackImporter` inspects archive entries, **Then** import fails closed before extracting any file.

### User Story 2 - Local Gemma 4 Tutor Chat with Room FTS RAG (Priority: P1)
Discrete AI Tutor action on Home or Lesson opens a clean, distraction-free chat screen showing offline model status, message thread, citations (`[Source: ...]`), cancel generation control, and clean composer (no floating overlay, no suggestion chip clutter above messages).

**Acceptance Scenarios**:
1. **Given** Gemma 4 `.litertlm` is installed, **When** user asks a grammar/writing question, **Then** app retrieves top-matching Room FTS curriculum snippets, generates streaming offline reply with citations, and supports mid-generation cancel + screen rotation survival.
2. **Given** no LLM is installed, **When** user opens Chat or submits a query, **Then** clean inline status + local FTS study-note answer + 1-tap link to Profile > Offline AI appears without blocking study.

### User Story 3 - Offline Speaking (AudioRecord + Whisper ONNX) & Writing Rubric Coaching (Priority: P1)
Speaking records locally (just-in-time `RECORD_AUDIO` permission), shows prep/speak countdowns, replays local recording, transcribes via Whisper ONNX when installed (editable auto-generated transcript), computes fluency metrics (WPM, pause ratio, filler words), and provides qualified non-official coaching. Writing tracks live word count, autosaves, and provides rubric-aligned local AI or deterministic structural feedback.

**Acceptance Scenarios**:
1. **Given** user taps Record on Speaking screen, **When** `RECORD_AUDIO` is denied, **Then** app explains permission need cleanly without crashing and lets user retry or type practice notes.
2. **Given** Whisper ONNX is loaded and user records speech, **When** transcription runs offline, **Then** transcript appears labeled `"Auto-generated (Whisper ONNX — editable)"` alongside duration, WPM, and explicit consent button before feedback.

## Functional Requirements
- **FR-008**: Define `LocalLlmEngine` and implement `LiteRtLmGemma4Engine` + deterministic grounded fallback helper.
- **FR-009**: Define `SpeechToTextEngine` and implement `WhisperOnnxSpeechEngine` (16 kHz PCM -> 80-bin Log-Mel spectrogram -> ORT encoder/decoder + BPE tokenizer) + `LocalAudioRecorder` (`AudioRecord` WAV writer).
- **FR-010**: Implement `SafStorageValidator` & `ModelImportRepository` (magic bytes, SHA-256, zip-slip/bomb check, ABI/RAM/storage gate, atomic swap, revocation detection).
- **FR-011**: Implement `CurriculumRagRetriever` (Room FTS4 search + prompt-injection-safe prompt builder with citations) and `RubricFeedbackAnalyzer` (qualified IELTS/iBT rubric coaching).
