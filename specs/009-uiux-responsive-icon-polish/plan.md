# Implementation Plan: Responsive UI/UX Polish & App Icon (`009`)

1. **Shared Primitives & Design Polish (`AppComponents.kt`)**
   - Add `ResponsiveContentContainer(maxWidth = 760.dp)` and helper `isExpandedWidth()` / `isMediumWidth()` based on `LocalConfiguration.current.screenWidthDp`.
   - Add `rememberTabularStyle(base)` (`fontFeatureSettings = "tnum"`) for glitch-free ticking timers/counters in `AudioPlayerBar`, `ExamScreen`, and `ProgressScreen`.
   - Upgrade `SelectableOptionRow`: add press-scale micro-interaction (`0.985f`, `PressFeedbackMs`, skipped when `reduceMotion = true`) alongside border/background animation.
   - Upgrade `AudioPlayerBar`: pill highlight on active speed (`0.85x`, `1.0x`, `1.25x`), `tnum` timestamps, `maxLines = 2` with `TextOverflow.Ellipsis` on track title.

2. **Menu-by-Menu Improvements (`Home`, `Learn`, `Practice`, `Exam`, `Results`, `Chat`, `Progress`, `Profile`)**
   - `HomeScreen`: wrap Hero CTA row in `FlowRow`; use 2-pane `Row` for Hero + Summary when `screenWidthDp >= 840`; constrain body max width; add `skipPartiallyExpanded = true` on `ExamSwitcher`.
   - `LearnViewModel` & `LearnScreens`: make `selectSection` clear `bookmarkFilter`; fix `"Semua"` chip `selected = !state.bookmarkedOnly && state.selectedSection == null`; add `AnimatedVisibility` + rotating chevron icon on `MaterialBankRow`; constrain reading width on `LearnScreen` & `ModuleDetailScreen`; protect `TopAppBar` title with `maxLines = 1, TextOverflow.Ellipsis`.
   - `PracticeScreen`: render `state.packs` in balanced 2-column rows on `screenWidthDp >= 840` (`ResponsiveContentContainer(maxWidth = 960.dp)`) and 1-column centered (`760.dp`) on narrower screens; ensure `AppButton` centers multi-line labels at 200% font scale.
   - `ExamScreen`: add thin linear progress strip below `TopAppBar` (`(currentIndex + 1f) / questions.size`); apply `tnum` on timer; make bottom action bar wrap cleanly via `FlowRow` when `fontScale >= 1.5f && screenWidthDp < 380` so `"Kembali"`, `"Cek"`, and `"Kumpulkan"` never clip; set `skipPartiallyExpanded = true` on `showPassageSheet` with vertical-scroll `heightIn(max = 560.dp)`; add status legend to `QuestionNavigatorSheetContent`.
   - `ResultsScreen`: add `FilterChip` bar (`Semua`, `Salah`, `Dilewati`, `Benar`) cleanly filtering `state.rows` across 85-140 items + constrain reading width (`840.dp`).
   - `ChatScreen`: protect `TopAppBar` subtitle with `TextOverflow.Ellipsis`; track `rememberLazyListState()` and `LaunchedEffect(state.messages.size, state.streamingReply.length)` scrolling smoothly (honoring `reduceMotion`); shape User vs Tutor cards with clean asymmetric margins inside centered `760.dp` column.
   - `ProgressScreen`: fix `"Akurasi per Seksi"` `Surface(modifier = Modifier.fillMaxWidth())`, drop trailing extra bottom `Spacer`, animate linear bars via `animateFloatAsState`, pair Summary + Trend side-by-side at `>=840dp`.
   - `ProfileScreen`: update compliance statement to accurately cite `15 paket penuh READY (1.725 soal)` + `Bank Materi OER PDF`; make `ToggleRow` fully clickable with `Modifier.toggleable(..., role = Role.Switch)` and `Switch(checked, onCheckedChange = null)` for larger 48dp+ touch comfort; wrap content in `ResponsiveContentContainer`.

3. **App Icon Generation (`tools/generate_app_icon.py`)**
   - Design bespoke geometric vector layers (`ic_launcher_foreground.xml`, tonal radial `ic_launcher_background.xml`, Android 13+ `ic_launcher_monochrome.xml`) matching `#0F6B66` Deep Teal, `#FAF9F6` Warm Paper, and `#E7C26C` Warm Amber bookmark accent.
   - Run 4x supersampled Pillow rasterizer generating `ic_launcher.png` (squircle) and `ic_launcher_round.png` across `mipmap-mdpi` (48px), `hdpi` (72px), `xhdpi` (96px), `xxhdpi` (144px), `xxxhdpi` (192px) + `docs/screenshots/app-icon-512.png`, inspect visually with `read_file`.

4. **Verification & Docs**
   - Add unit/Robolectric tests in `UiAndAccessibilityTest.kt` for `320dp @ 2.0x` narrow font scaling, tablet `840dp` wide layout, `ResultsScreen` filtering, `LearnViewModel` filter chip exclusivity, `ProfileScreen` `ToggleRow`, and updated compliance copy.
   - Run `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleRelease`, deploy updated R8 Release `v1.1.1` (`versionCode = 3`) to `SM-G988B`, inspect launcher icon & updated screens, update docs, commit, push, and publish updated GitHub Release.
