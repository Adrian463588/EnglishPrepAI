# DESIGN.md — Human-centered, minimal Android UI/UX
Version 2026-11-16 | Native Compose Material 3. Goal: polished, calm, highly readable, NOT AI slop.

## Design direction
Functional editorial simplicity: neutral surfaces, one restrained accent (deep teal), 8dp rhythm, typography hierarchy, quiet dividers. Support light/dark + dynamic color when contrast survives. Avoid oversized gradients, glow, redundant progress widgets, excessive cards, random chips, floating chatbot obscuring questions, emoji-only navigation, generic decorative illustrations, fake streaks.

## Navigation & interaction budget
Persistent five destinations: Home / Learn / Practice / Progress / Profile. Home shows clear selected exam at top; one exam switcher tap -> selection -> back. Desired taps from Home: <=2 to resume, <=3 to topic drill, <=3 to start a mock, <=2 to open chatbot. Counts exclude actual answers and system document picker interactions. Measure real navigation in UI tests, don't optimize at expense of clarity.

## Screen contracts
Home: selected exam, primary Resume or Start CTA, next recommended topic, concise progress, secondary AI Tutor action.
Learn: test -> skill -> topic -> lesson (concept -> 1 example -> interactive 3–5 item drill -> link to mistakes).
Practice: section filters, short drills, five package cards with Sample/Ready labels, duration, available items and download size; explicit strict vs learning modes.
Question: top back/quit, progress X/Y, readable prompt and assets, one answer control pattern, anchored Next, flag for review; no distracting navigation. Show timer in mock mode. Accessibility: passage/font resize and keyboard navigation.
Listening: clear playback affordance, status, progress, audio speed only in practice mode, listening limits configurable per exam mode; controls disabled in strict mock if blueprint says restricted.
Writing: prompt/context, word count, autosave, expandable rubric, submit -> qualified feedback or deferred if model missing.
Speaking: microphone permission just-in-time, preparation + capture timers, waveform optional, recording replay, editable transcript labeled auto-generated, feedback only after consent.
AI Chat: plain header with offline model status, thread, simple composer, source references. No chips overlaying messages. Model management is in Profile > Offline AI, not duplicated in chat.
Progress: exam-specific section accuracy, recent sessions, reviewed mistakes, simple bars/line plots with useful empty states; don't create inferred official scores.
Profile: language, accessibility, storage/SAF, import packs, model selection, reminders, audio, licenses, privacy, delete all data. No account/signup.

## Components and microinteractions
- AppButton, ExamSelector, ModuleRow, PackageRow, QuestionCard, AudioPlayerBar, ProgressSummary, InlineFeedback, OfflineModelState.
- State transitions: pressed 100–150ms, check 150–200ms, screen transitions 180–250ms; disable nonessential animation when reduced motion. Never delay input because of animation.
- Show autosave status quietly; haptics only when enabled for submission/completion; one snackbar for reversible actions; actionable error with retry.
- 48dp minimum tap targets, >=8dp spacing between touch targets, no text clipping on font scale 2x, clear screen-reader semantics and focus sequence.
- Outline/error/red only for meaningful state. Correct answer green is supplemental to icons/text. Respect IME in Writing and Chat.

## UI acceptance tests
- From Home -> mock selection <=3 taps, model import via Profile -> Offline AI -> Import (system picker excluded).
- Chat room contains no generic pre-prompt chip row, duplicate reminder entry, or floating overlapping action.
- Device sizes: 360x640, 412x915, tablet 800dp; portrait/landscape during exam and audio.
- Preview and screenshot tests for light/dark, empty/error/loading/ready, large text; manual TalkBack checks.
- Screenshot diff review by human/vision-agent, and Compose UI tests for navigation and overlap; do not claim visual validation without actual screenshots.

## Definition of Done
- Semua alur utama berjalan sepenuhnya tanpa internet setelah aset diimpor.
- Build debug berhasil, unit/instrumented tests lulus; manual QA pada ponsel nyata.
- Tidak ada crash untuk SAF URI hilang, file corrupt, izin dicabut, low storage, audio focus, background/rotation.
- Aksesibilitas TalkBack, font scaling 200%, target sentuh ≥48dp, kontras dan reduced motion diperiksa.
- Tidak ada klaim nilai resmi, bank soal berhak cipta yang disalin, atau model yang dipalsukan.
- Dokumentasi, changelog, dan checklist verifikasi diperbarui berdasarkan hasil aktual.
