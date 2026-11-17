# Design System Specification (`docs/ui-ux/design-system.md`)

- **Aesthetic Direction**: Functional Editorial Simplicity (calm warm paper / slate surfaces paired with a single restrained Deep Teal focal accent; zero AI gradients, zero decorative bento slop, hairline dividers over stacked cards).
- **Token Sources**:
  - FigCraft JSON tokens: `design/tokens.json` (`version = 1.3.0`)
  - Compose colors, typography, shapes, spacing & motion: `app/src/main/java/com/englishprep/ai/ui/theme/Color.kt`, `Tokens.kt`, `Theme.kt`

## 1. Color Roles (`Color.kt` & `design/tokens.json`)

| Role | Light Mode Hex | Dark Mode Hex | Usage Rule |
|---|---|---|---|
| `primary` | `#0F6B66` | `#66D1CA` | Primary CTA, active progress indicator, active radio/switch tint |
| `onPrimary` | `#FFFFFF` | `#003734` | Labels/icons on primary fill |
| `primaryContainer` | `#D0ECE9` | `#0B4F4B` | Tonal hero card (`Home`), active bottom nav indicator pill |
| `onPrimaryContainer` | `#063330` | `#B5EFEA` | Text/icons inside primary container |
| `secondary` | `#47605E` | `#B0C9C6` | Supporting filter/chip state and secondary metadata |
| `background` | `#FAF9F6` | `#111415` | Main viewport ground (Warm Paper / Dark Slate) |
| `surface` | `#FAF9F6` | `#15191A` | TopAppBar, sheets, Scaffold base |
| `surfaceContainerLow` | `#F3F1EC` | `#1D2223` | Tonal Reading banner, Audio bar, Practice pack cards, Passage pane |
| `surfaceContainerHigh` | `#EAE7E0` | `#262C2E` | Modal dialog surfaces & elevated sheet handles |
| `onSurface` | `#1A1D1D` | `#E8ECEB` | Primary editorial text |
| `onSurfaceVariant` | `#4F5756` | `#AEB8B6` | Secondary descriptions, metadata, timestamps |
| `outline` | `#D0CCC4` | `#394143` | 1dp subtle hairline dividers & outlined selectors |
| `success` / `successContainer` | `#1B6E43` / `#D4F0DF` | `#8CD9AB` / `#114B2C` | Checked correct state & verified status indicators |
| `error` / `errorContainer` | `#B3261E` / `#F9DEDC` | `#F2B8B5` / `#8C1D18` | Incorrect answer feedback & destructive confirmations |

## 2. Typography Scale (`Tokens.kt` - `AppTypography`)

- `headlineMedium`: 24sp / 32sp line-height / SemiBold / -0.2sp tracking (screen headers)
- `headlineSmall`: 20sp / 28sp / SemiBold (section emphasis & module headers)
- `titleLarge`: 18sp / 25sp / SemiBold (card headers, passage titles)
- `titleMedium`: 16sp / 22sp / Medium (exam titles, module list titles, TopBar timers with `tnum`)
- `bodyLarge`: 16sp / 24sp / Normal (reading passages, question stems, chat bubbles)
- `bodyMedium`: 14sp / 20sp / Normal (secondary descriptions, hints)
- `labelLarge`: 14sp / 20sp / SemiBold (primary CTA labels)
- `labelMedium`: 12sp / 16sp / Medium (metadata, word counters, mastery %)
- `labelSmall`: 11sp / 14sp / Medium (TopAppBar model subtitle, compact status chips)
- Tabular numerals enabled via `TextStyle.tabular()` (`fontFeatureSettings = "tnum"`) on exam timers, counters, accuracy percentages, and pack statistics.

## 3. Spacing, Shapes, Motion & WindowInsets

- **Spacing Scale (`AppSpacing`)**: `xs = 4.dp`, `sm = 8.dp`, `md = 16.dp`, `lg = 24.dp`, `xl = 32.dp`, `xxl = 48.dp`, `minTouchTarget = 48.dp`.
- **Corner Radius (`AppShapes`)**: `extraSmall = 4.dp`, `small = 8.dp`, `medium = 12.dp`, `large = 16.dp`, `extraLarge = 24.dp`.
- **Motion (`MotionTokens`)**: `PressFeedbackMs = 120`, `AnswerCheckMs = 175`, `ScreenTransitionMs = 220` (disabled immediately when `reduceMotion = true`).
- **System Bars (`Theme.kt` + `MainActivity.kt`)**: Edge-to-edge enabled with contrast-matched `isAppearanceLightStatusBars` and `isAppearanceLightNavigationBars`; top-level routes exclude IME from root `Scaffold`, while secondary routes pass `WindowInsets(0, 0, 0, 0)` so nested `Scaffold`s handle `statusBars`, `navigationBars`, and `.imePadding()` once.
