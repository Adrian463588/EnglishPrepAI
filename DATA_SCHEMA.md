# DATA_SCHEMA.md — Room entities, JSON content and migration rules
Version 1 (design specification; not implemented). Primary keys UUID/opaque strings; use enums stored as validated strings; all timestamps UTC epoch millis. Foreign keys indexed; unique (examId, packId) and (sessionId, itemId).

## Entity definitions
ExamProfile: id PK [ITP_L1, IBT_2026, IELTS_ACADEMIC]; name, edition, scoringPolicyVersion, formatReferenceUrl.
CourseModule: id PK, examId FK, section, parentId nullable, title, objective, markdownRef, difficulty, sortIndex, license, provenance.
ContentPack: id PK, examId FK, edition, title, manifestVersion, status [SAMPLE, INCOMPLETE, READY], license, provenance, importedAt, installedPath, checksum.
Question: id PK, packId FK, moduleId nullable, section, taskType, prompt, passageId nullable, audioAssetId nullable, responseSchemaJson, correctAnswerJson nullable, explanation, rationaleSource, difficulty, sortIndex, points, sourceLicense.
QuestionOption: id PK, questionId FK, label, text, sortIndex. Use only where applicable.
Passage: id PK, packId FK, title, body, sourceLicense, readingOrder.
MediaAsset: id PK, packId FK, relativePathOrUri, kind [AUDIO, IMAGE, TRANSCRIPT], sha256, byteSize, durationMs nullable, caption, license.
ExamSession: id PK, examId FK, packId nullable, mode [PRACTICE, STRICT], startAt, deadlineAt nullable, endAt nullable, lastSavedAt, state [ACTIVE, SUBMITTED, ABANDONED], elapsedMonotonicCheckpointMs nullable, scorePolicyVersion.
UserAnswer: id PK, sessionId FK, questionId FK, answerJson, updatedAt, isFlagged, submittedAt nullable, autoCheckResultJson nullable.
Evaluation: id PK, sessionId FK, section, rawCorrect, rawTotal, scaledScore nullable, officialEquivalent boolean default false, estimateMethod nullable, aiFeedbackJson nullable, createdAt.
StudyProgress: id PK, examId FK, moduleId FK nullable, attempts, correct, reviewedAt, masteryEstimate nullable, updatedAt.
ChatThread: id PK, examId nullable, title, createdAt, updatedAt.
ChatMessage: id PK, threadId FK, role [USER, ASSISTANT, SYSTEM], text, citationsJson nullable, modelId nullable, createdAt.
ModelInstall: id PK, displayName, family, modelVariant, runtime, format, quantization, sha256, sizeBytes, privateStoragePath, sourceDocumentUri nullable, status, installedAt, licenseId.
SpeechRecording: id PK, sessionId FK, questionId FK, privateFilePath, durationMs, transcript nullable, feedbackJson nullable, createdAt.
ImportJob: id PK, kind, sourceUri, state, errorCode nullable, createdAt, completedAt nullable.
UserSetting: use Proto/Preferences DataStore (selectedExam, language, audioSpeedPractice, reminder settings, darkTheme, modelId, reduceMotion).

## Migration and consistency
- Schema export enabled, migration tests every version; no destructive migration in production.
- DO NOT duplicate authoritative `correctAnswer` into mutable user answers.
- Scoring references immutable pack revision and policy version; protect completed session integrity.
- Cascade delete only on explicitly user-deleted sessions/packs, provide data export, avoid silent deletion of answers.
- Question/source asset links integrity verified on install, refs limited to pack root.
- DataStore for small preferences only, not big banks or files.

## Pack item contract (illustrative)
```json
{
  "id": "ITP-L1-01-Q001",
  "taskType": "CHOICE_SINGLE",
  "section": "STRUCTURE",
  "prompt": "The professor ___ the results yesterday.",
  "options": ["present", "presented", "presenting", "presents"],
  "answer": "presented",
  "explanation": "Yesterday signals simple past tense.",
  "difficulty": "A2-B1",
  "license": "Original",
  "reviewStatus": "DRAFT"
}
```

## SQL-like indices
`Question(packId,section,sortIndex)`, `ExamSession(examId,startAt)`, `UserAnswer(sessionId,questionId UNIQUE)`, `StudyProgress(examId,moduleId UNIQUE)`, `ChatMessage(threadId,createdAt)`. Use Room FTS5/FTS4 based on Android support for source-backed retrieval; index text excerpts, not raw model binary.

## Storage paths
Room database `databases/english_prep.db`; app-private `files/models/`, `files/packs/`, `files/recordings/`, `cache/inference/`. SAF document/tree URI is external authority and may be revoked. Never save model binaries in Room or use unrestricted filesystem access.

## Definition of Done
- Semua alur utama berjalan sepenuhnya tanpa internet setelah aset diimpor.
- Build debug berhasil, unit/instrumented tests lulus; manual QA pada ponsel nyata.
- Tidak ada crash untuk SAF URI hilang, file corrupt, izin dicabut, low storage, audio focus, background/rotation.
- Aksesibilitas TalkBack, font scaling 200%, target sentuh ≥48dp, kontras dan reduced motion diperiksa.
- Tidak ada klaim nilai resmi, bank soal berhak cipta yang disalin, atau model yang dipalsukan.
- Dokumentasi, changelog, dan checklist verifikasi diperbarui berdasarkan hasil aktual.
