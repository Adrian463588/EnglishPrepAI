# Design System — English Prep AI (`docs/design-system.md`)

**Philosophy**: Functional editorial simplicity — neutral warm-paper and slate-dark surfaces paired with one restrained Deep Teal accent, structured by spacing, typographic scale, and hairline dividers rather than boxed cards.

## Color Roles (`Color.kt` & `Theme.kt`)
- **Primary Accent**: Deep Teal (`#0F6B66` light / `#66D1CA` dark) reserved for primary CTAs, active tabs, selected options, and active progress indicators.
- **Neutral Surfaces**:
  - Light: Warm Paper `#FAF9F6`, Surface Low `#F3F1EC`, Ink `#1A1D1D`, Muted `#4F5756`, Hairline `#D0CCC4`.
  - Dark: Slate Background `#111415`, Surface `#15191A`, Surface Low `#1D2223`, Ink `#E8ECEB`, Muted `#AEB8B6`, Hairline `#394143`.
- **Semantic Status**: Success (`#1B6E43` light / `#8CD9AB` dark) and Error (`#B3261E` light / `#F2B8B5` dark) paired always with status icons and text labels (never color alone).

## Spacing Rhythm (`Tokens.kt` -> `AppSpacing`)
- Scale: `xs = 4.dp`, `sm = 8.dp`, `md = 16.dp`, `lg = 24.dp`, `xl = 32.dp`, `xxl = 48.dp`.
- Screen margins: `16.dp` (`md`) horizontal padding on phones, constrained to centered reading columns (`720–960.dp`) via `ResponsiveScreenContainer`.
- Touch targets: `minTouchTarget = 48.dp` on every interactive button, chip, icon button, and list row.

## Surface & Card Hierarchy Rules
1. **Default container = Whitespace + Hairline Divider**: Curriculum items, next lesson suggestions, settings groups, progress sessions, and results review items use flat rows separated by subtle `HorizontalDivider`s rather than boxed cards.
2. **Primary Focal Surface**: At most one quiet tonal surface (`surfaceContainerLow` or subtle `primaryContainer` tint) marks the active focal task (e.g., active exam resume block on Home, compact audio bar during Listening, summary block on Results).
3. **Zero Nested Cards**: Interactive option rows (`SelectableOptionRow`) sit directly on the question canvas without an enclosing bordered card around the prompt.

## Motion & Accessibility (`MotionTokens`)
- `PressFeedbackMs = 120`, `AnswerCheckMs = 175`, `ScreenTransitionMs = 220` (automatically zeroed when `reduceMotion = true`).
- `TextStyle.tabular()` (`fontFeatureSettings = "tnum"`) enforced on timers, countdowns, durations, and scores.
- Dynamic `enableEdgeToEdge` bar appearance (`isAppearanceLightStatusBars` / `isAppearanceLightNavigationBars`) synced live with active Light/Dark app theme.
