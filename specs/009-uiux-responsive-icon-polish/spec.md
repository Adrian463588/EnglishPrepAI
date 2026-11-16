# Feature Specification: Menu-by-Menu Responsive UI/UX Polish, Micro-Interactions & Adaptive App Icon

**Feature Branch**: `009-uiux-responsive-icon-polish`
**Created**: 2026-11-16
**Status**: Approved for Execution

## Complete Menu Inventory & Defect Audit

1. **Top-Level Navigation (`MainActivity.kt` / `AppDestinations.kt`)**
   - Uses `NavigationBar` on `Compact` (`<600dp`) and `NavigationRail` on `Medium/Expanded` (`>=600dp`), hiding cleanly inside deep study/exam/chat flows.
   - Audit finding: Content routes (`Home`, `Learn`, `Practice`, `Progress`, `Profile`, `Results`, `ModuleDetail`, `Chat`) expand edge-to-edge without a readable max measure on `600dp+` / `840dp+` tablets and landscape; lacks a shared responsive width-constrained container helper.

2. **Menu 1: Beranda (`HomeScreen` + `ExamSwitcher` ModalBottomSheet)**
   - Audit finding: Hero action `Row { AppButton; TextButton }` risks horizontal cramping at `320dp` width + `2.0x` font scale; `HomeScreen` lacks 2-column responsive layout on `>=840dp`; `ExamSwitcher` sheet lacks `skipPartiallyExpanded = true` + test-type summary distinction.

3. **Menu 2: Materi (`LearnScreen`, `Bank Materi OER`, `ModuleDetailScreen`)**
   - Audit finding: Selecting `Tersimpan` left `Semua` chip selected simultaneously (`state.selectedSection == null` lacked `!state.bookmarkedOnly`), and tapping a section chip in `LearnViewModel` did not reset `bookmarkFilter`; `MaterialBankRow` expands abruptly without `AnimatedVisibility` + chevron rotation feedback; `ModuleDetailScreen` top bar title needs ellipsis protection at `2.0x` scale and responsive max reading width.

4. **Menu 3: Latihan (`PracticeScreen` + `PackageRow`)**
   - Audit finding: Single-column packs stretch overly wide on `840dp+` tablets; needs clean responsive 2-column grid on Expanded (`>=840dp`) width and centered `840dp` container + crisp exam summary count indicator.

5. **Menu 4: Simulasi Ujian (`ExamScreen`, `QuestionNavigatorSheetContent`, `PassagePane`)**
   - Audit finding: TopBar timer shifts digits slightly due to proportional numerals (needs `fontFeatureSettings = "tnum"`) and lacks thin linear exam progress bar; BottomBar `Row` (`Kembali` + `Cek` + `Lanjut/Kumpulkan`) can crowd at `320dp` + `2.0x` font scale (needs adaptive `FlowRow` wrapping under tight width/font scale); `showPassageSheet` lacked `skipPartiallyExpanded = true`; Navigator lacks compact status legend (`Aktif / Terjawab / Ditandai`).

6. **Menu 5: Hasil & Pembahasan (`ResultsScreen`)**
   - Audit finding: Scrolling through `140` ITP items or `120` iBT items lacks a quick status filter (`Semua`, `Salah`, `Dilewati`, `Benar`) right above item explanations; reading cards expand without max width constraint on tablets.

7. **Menu 6: Tutor AI Offline (`ChatScreen`)**
   - Audit finding: TopAppBar model subtitle needs single-line ellipsis safety on `320dp`; message list lacks smooth auto-scroll to newest replies / streaming response; bubble alignment needs subtle asymmetric horizontal margin and max width cap (`760.dp`) on wide screens.

8. **Menu 7: Analitik Progres (`ProgressScreen`)**
   - Audit finding: `"Akurasi per Seksi"` `Surface` lacked `Modifier.fillMaxWidth()`, trailing `Spacer(AppSpacing.md)` left asymmetric bottom padding inside the card, section progress bars lacked `animateFloatAsState`, and wide tablets lacked side-by-side summary + trend pairing.

9. **Menu 8: Profil, AI Offline & Pengaturan (`ProfileScreen`)**
   - Audit finding: Stale compliance copy at line 409 still stated `"berstatus Sample"` instead of reflecting `15 paket penuh READY (1.725 soal) & Bank Materi OER`; `ToggleRow` only toggled on direct tap to small `Switch` rather than making the entire `48dp+` row `toggleable`; layout lacked max readable width cap on tablets.

10. **Adaptive Launcher Icon (`mipmap-*` + `drawable/ic_launcher_*`)**
    - Audit finding: `ic_launcher_foreground.xml` is only a 3-rectangle placeholder, `ic_launcher_monochrome.xml` is absent for Android 13+ themed icons, and legacy `mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher*.png` files are missing.

## Invariants
- Follow `android-compose-anti-slop` & `adaptive`: semantic M3 tokens only, zero arbitrary gradients or AI-slop decoration, >= `48dp` interactive targets, `2.0x` font scale safety, TalkBack semantics, `reduceMotion` respected on every new animation/transition.
- Keep DRY/SOLID/KISS/YAGNI & Ponytail discipline: share a single `ResponsiveContentContainer` helper rather than duplicating `widthIn` logic across 8 files.
- Keep backward compatibility with existing Compose test tags, semantics, and `Screen(..., modifier)` signatures.

## Functional Requirements
- **FR-030**: Create shared responsive layout helpers (`ResponsiveContentContainer`, adaptive grid helper, `tabularStyle`) in `AppComponents.kt`; upgrade micro-interactions on `SelectableOptionRow`, `AppButton`, `AudioPlayerBar` (`tnum` timers), and smooth animations respecting `LocalSemanticStatusColors.current.reduceMotion`.
- **FR-031**: Fix `LearnViewModel` filter exclusivity (`selectSection` clears `bookmarkFilter`) and `LearnScreen` chip selection + animated `MaterialBankRow` expansion + `ModuleDetailScreen` responsive reading measure & ellipsis title.
- **FR-032**: Upgrade `HomeScreen` (responsive 2-column layout on Expanded + `FlowRow` CTA wrapping at 2x + `skipPartiallyExpanded` `ExamSwitcher`) and `PracticeScreen` (responsive 2-column grid on Expanded `>=840dp`).
- **FR-033**: Upgrade `ExamScreen` (`tnum` TopBar timer + thin progress bar + adaptive BottomBar safe at `320dp @ 2.0x` + `skipPartiallyExpanded` Passage Sheet + compact status legend in Navigator).
- **FR-034**: Upgrade `ResultsScreen` (interactive filter chips `Semua / Salah / Dilewati / Benar` across 85-140 items + responsive max width) and `ChatScreen` (ellipsis header, auto-scroll to newest/streaming reply, asymmetric readable bubbles).
- **FR-035**: Upgrade `ProgressScreen` (`fillMaxWidth()` section card fix, animated section progress bars, Expanded 2-column summary+trend) and `ProfileScreen` (updated READY/OER compliance text, full-row `toggleable` switches, responsive max width).
- **FR-036**: Generate bespoke **Editorial Open Folio + Listening Wave + Nib** Android Adaptive App Icon (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`, `ic_launcher_monochrome.xml`, `mipmap-anydpi-v26` + high-DPI antialiased PNGs across `mdpi`..`xxxhdpi` + 512px store preview `docs/screenshots/app-icon-512.png`).
