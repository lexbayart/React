# React

**Red pill for your brain.**

When you're stuck in a game, series, conversation, or work dead end — React helps you reset.

## How it works
1. Open the app
2. Choose the emoji that matches your current state (8 options)
3. Get 4 actions you've previously chosen for this state
4. Tap an action — the app closes, and you go do it

The more often you choose an action, the higher its chance to appear next time.

## Features
- **8 states**: Gamer, Viewer, Talker, Sleeper, Zombie, Stuck, Anxious, Sad
- **Weighted random**: Actions you choose more often appear more frequently
- **Add custom actions**: Tap ➕ to add your own escape route
- **Reshuffle**: Tap 🎲 to get a new set without repeats
- **Statistics**: Long-press the title on the main screen to see your stats
- **Export**: Share your stats as JSON and CSV files
- **Dark/Light theme**: Follows your system setting
- **Zero distractions**: No notifications, no questions, no accounts

## Tech stack
- Kotlin + Jetpack Compose
- Room for local storage
- Material 3
- Android 13 (API 33)

## Development
1. Clone the repository
2. Open in Android Studio
3. Connect a phone with USB debugging
4. Press Run

## Project structure
- `MainActivity.kt` — entry point
- `MainViewModel.kt` — state management
- `ui/screens/` — screens (StateScreen, ActionScreen, StatsScreen)
- `ui/theme/` — theme and typography
- `data/database/` — Room Database, DAOs, Entities
- `data/repository/` — selection logic (Weighted Random)
- `utils/` — haptics, sounds, JSON/CSV export

## Database schema
- **states**: 8 predefined states with emoji and name
- **actions**: User-defined escape actions
- **state_action_usage**: Tracks how many times each action was chosen per state
- **logs**: Timestamped record of every action selection

## License
Private
