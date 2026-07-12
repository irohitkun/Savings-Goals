# Savings Goals

A local-only savings goal tracker. No UPI/payment integration — your money stays exactly
where it already is (your bank account). This app just tracks progress toward goals you
define, with contributions you log manually.

## Stack
- Kotlin + Jetpack Compose (Material 3)
- Room (local SQLite) — all data stays on-device, nothing leaves your phone
- No network permissions requested at all

## Features
- Multiple goals at once (name, emoji, target amount)
- Progress bar + percentage per goal
- Manual "Add money" entries with optional notes
- Full contribution history per goal, swipe-free delete on any entry
- Delete a goal (with confirmation) — cascades and removes its history

## Opening the project
1. Open Android Studio (Koala or newer recommended).
2. **File → Open** → select the `SavingsGoals` folder.
3. Let Gradle sync (it will generate the wrapper automatically if prompted).
4. Run on an emulator or your device (minSdk 26 / Android 8.0+).

## Where to extend next
- `data/Goal.kt` has a `targetDateMillis` field already wired through the DB — the UI
  just doesn't collect it yet. Add a date picker to `AddGoalDialog` in `GoalListScreen.kt`
  to use it (e.g. show "12 days left" on the card).
- If you ever want semi-automated logging without touching real payment rails, you could
  add a `NotificationListenerService` that reads your UPI app's notification text (amount +
  merchant) and pre-fills the "Add money" dialog for you to confirm — still no money
  actually moves through the app, it just saves you typing.
