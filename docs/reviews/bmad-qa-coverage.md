# BMAD Review — QA & Safety Officer: Required Test Matrix

- **Reviewer Role**: BMAD QA Officer
- **Authoritative Spec**: `AGENTS.md` Required Test Matrix

## Test Coverage Gates
1. **Quiz Engine (JVM Unit Tests)**: Single MCQ, Multi-Select, FillBlank (case/space/plural & word-limit enforcement), Matching, Reorder, ErrorIdentification, SentenceBuild, skipped items, answer changes, deterministic rationales.
2. **Timing & State Survival**: Monotonic elapsed checkpoint calculation across background/clock drift; `SavedStateHandle` + Room persistence verification.
3. **Media & Recording**: `STRICT` vs `PRACTICE` replay/speed gates; missing/corrupt asset error recovery; AudioFocus duck/pause behavior; WAV header encoding + WPM/filler calculation.
4. **SAF & Pack Security**: Zero-byte rejection, zip-slip `../` rejection, decompression-bomb cap rejection, low-storage rejection, SHA-256 mismatch, revoked URI error state without crash.
5. **AI & RAG Isolation**: Prompt-injection neutralization on retrieved snippets, missing-model graceful fallback, mid-generation cancellation, non-official disclaimer presence.
6. **UI & Physical Device Smoke**: Compose UI tests for tap counts, `48dp` bounds, `fontScale = 2.0f`, empty Chat chip absence, plus Gradle debug APK build + `adb` install/launch/screenshot inspection on connected `SM-G988B` (Android 13, arm64-v8a).
