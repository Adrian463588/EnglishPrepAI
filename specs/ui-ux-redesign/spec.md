# Spec: Complete UI/UX Audit, Redesign & Anti-AI-Slop Refactoring (`specs/ui-ux-redesign`)

## Problem Statement
Live device inspection on Samsung `SM-G988B` (Android 13, Light & Dark modes) and source audit of the Compose layer identified P0 and P1 UX/UI defects across Home, Learn, Practice/Exam, Results, Chat, Progress, and Profile:
1. **P0 Light-Mode System Bar Contrast**: Switching in-app theme to `Light` while OS night mode is Dark left light/white status bar icons illegible against `#FAF9F6` Warm Paper (`audit_light_home.png`).
2. **P1 Screen Clutter & Card-Inside-Card Patterns**:
   - **Home**: 4 competing bordered cards, promotional hero tagline, repeated `scaleLabel` strings in 3 places, missing genuine session question count (`X/Y soal`), and bulky `OfflineModelState` box for Tutor AI.
   - **Learn**: 3-row wrapped `FlowRow` filter chips (due to `"Structure & Written Expression"`), repetitive explanatory blurb, mixed divider list + 31 bordered Material Bank cards below modules, and raw internal ID (`ITP-MOD-01`, `DRILL-01`, `CHOICE_SINGLE`) exposure in detail view.
   - **Practice / Exam**: Oversized `AudioPlayerBar` displaying internal asset ID (`ITP01-L01`) and 3 full bordered speed boxes; nested bordered `QuestionCard` around bordered `SelectableOptionRow`s displaying `CHOICE_SINGLE`; giant empty void above asymmetrical bottom controls.
   - **Profile & Offline AI**: Top ~2 viewports consumed by AI import banners, 3 large stacked buttons, verbose Hugging Face explanations, and catalog cards before Settings/Preferences/Data appear; no separation between general `Profile` and `Offline AI Model Manager`.
   - **Progress & Results**: Bordered cards wrapping bordered inner cards (`ResultsScreen` item cards with embedded `InlineFeedback`); Progress repeating exam scale strings instead of crisp 4-section hierarchy.
   - **Chat**: Raw `citationsJson` array strings when populated; cramped top bar when `"Bersihkan"` and `"Impor AI"` appear together; Home opening generic chat pre-filled a specific module query.

## Documented Source Conflicts & Resolutions (`Phase 0`)
Per Phase 0 rules, conflicts between prompt recommendations (`2026` guidelines) and repository contracts (`AGENTS.md`, `DESIGN.md`, `ARSITEKTUR.md`, `PRD.md`, `gradle/libs.versions.toml`) are resolved transparently before editing:
1. **Navigation 3 (`androidx.navigation3`) & M3 Expressive vs Stable Existing Stack (`navigation-compose 2.8.8`, `compose-bom 2025.02.00`)**:
   - *Conflict*: Repository `AGENTS.md`, `ARSITEKTUR.md`, and project rules mandate zero unrequested dependency replacements or full architecture rewrites, while keeping type-safe Navigation Compose + Material 3.
   - *Resolution*: Preserve `navigation-compose 2.8.8` with type-safe `@Serializable AppRoute` (adding `AppRoute.ModelManager` for `Profile -> Offline AI -> Manage Models`) and Material 3 BOM tokens with restrained depth, semantic colors, Slot APIs, `NavigationBar`/`NavigationRail` adaptive switching (`>=600dp`), dynamic edge-to-edge `SystemBarStyle`, and reduced-motion-respecting transitions.
2. **App Language & Genuine Metrics vs English Mockup Labels**:
   - *Conflict*: Prompt examples use English copy (`Continue learning`, `12 of 85 questions`) while `PRD.md` and UI copy are in **Bahasa Indonesia** and must never show fabricated metrics (`12 of 85` when a session has `0 of 140`).
   - *Resolution*: Maintain consistent **Bahasa Indonesia** learner-facing labels paired with **genuine reactive Room counts** (`activeSessionAnsweredCount / activeSessionTotalCount`, `sectionRows`, `weakModules`, `overallAccuracyPercent`).
3. **Material Bank (31 OER Digests) & R8 Security Summary Preservation**:
   - *Conflict*: Aggressive minimalism could delete OER Material Bank PDF digests (`specs/008`) or R8 Integrity readout (`specs/005`), violating "Preserve working features".
   - *Resolution*: Keep all 31 OER digests cleanly scoped in Learn via a dedicated single-row filter chip (`[Bacaan OER]`) instead of dumping 31 bordered cards below modules, and place R8 security posture cleanly under **Privasi & Keamanan** in `ProfileScreen` as a clean expandable/concise row + detail dialog.

## Target Design & Navigation Contracts
- **Home**: Displays (1) Active exam chip selector, (2) Primary Continue/Start block with genuine `answered/total` progress and ONE clear primary CTA (`Lanjutkan`/`Mulai`), (3) Next lesson row + divider, (4) Concise overall accuracy (only when sessions > 0), (5) Single-row `Tutor AI ->` link.
- **Learn**: Single-row horizontally scrollable filter strip (`Semua`, `Listening`, `Structure`, `Reading`, `Tersimpan`, `Bacaan OER`); concise divider list rows (`Title`, `Section · Difficulty · Duration`). Clean typographic editorial blocks in `ModuleDetail` with human-readable titles.
- **Practice / Exam**: Compact audio control (`[▶/⏸]`, human title, scrubber/time, compact `[1.0x ▾]` speed selector menu in Practice, optional inline Transcript toggle); clean divider-separated question prompt without nested bordered cards or raw internal IDs; persistent accessible bottom bar (`Sebelumnya`, optional `Periksa`, anchored `Berikutnya`/`Kumpulkan`) with clean edge-to-edge + IME inset handling.
- **Profile & Model Manager**: Grouped clean list sections (`Preferensi`, `Konten Offline`, `AI Offline` -> `Kelola Model AI`, `Privasi & Data`); dedicated `AppRoute.ModelManager` screen for installed models, Hugging Face recommended list, SAF import actions, and expandable technical specs (runtime, format, quantization, SHA-256, disk/RAM).
- **AI Chat**: Uncluttered header (`Back`, `Tutor AI`, single-line model status, single `Bersihkan` action when messages exist), clean empty state & messages, human-formatted `Sumber: ...` citations, offline link to `ModelManager` when LLM unloaded, zero floating chip rows.
- **Progress & Results**: Strict 4-section hierarchy on Progress (`Ringkasan`, `Performa per Seksi`, `Percobaan Terbaru`, `Perlu Diperkuat`); non-nested divider review rows with FilterChips (`Semua`, `Salah`, `Dilewati`, `Benar`) on Results.
