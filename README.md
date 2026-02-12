# Cebolao Lotofacil Generator

**A professional statistical analysis tool for Lotofacil.**
Because luck is for amateurs, but probability is for engineers.

![Android](https://img.shields.io/badge/Android-API_26+-green?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple?style=flat-square&logo=kotlin)
![Compose](https://img.shields.io/badge/Jetpack_Compose-2024.06-blueviolet?style=flat-square&logo=jetpackcompose)
![License](https://img.shields.io/badge/license-MIT-yellow?style=flat-square)
![Status](https://img.shields.io/badge/Status-Production_Ready-brightgreen?style=flat-square)

## Key Features

- **Intelligent Generator:** Advanced algorithm using statistical filters based on historical data.
- **Performance Auditor:** Validate generated games against the entire history of draws.
- **Deep Analytics:** Visual insights into hot/cold numbers, parity, and sum patterns.
- **Modern UI:** Built 100% with Jetpack Compose, featuring Dark/Light API-driven theming.
- **Smart Validation:** Real-time feedback on filter constraints and probability rules.

## Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | [Kotlin 2.0](https://kotlinlang.org/) + Coroutines + Flow |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3) |
| **Architecture** | [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) + MVVM |
| **Dependency Injection** | [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) |
| **Persistence** | [Room](https://developer.android.com/training/data-storage/room) & [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) |
| **Network** | [Retrofit](https://square.github.io/retrofit/) + Kotlinx Serialization |
| **Testing** | JUnit 5, Mockk, Espresso, Compose Test Rule |

## Project Structure

This project follows a strict separation of concerns:

- `app/src/main/java/com/cebolao/lotofacil/`:
- `data/`: Data Layer. Repositories, DataSources, Mappers.
- `domain/`: Domain Layer. UseCases, Repository Interfaces, Models.
- `ui/`: Presentation Layer. ViewModels, Composables, Theme.
- `di/`: DI Modules. Hilt configurations.

## Getting Started

1. **Clone**: `git clone https://github.com/cebola-studios/cebolao-generator.git`
2. **Open**: Import into Android Studio Koala (or newer).
3. **Sync**: Allow Gradle to fetch dependencies.
4. **Run**: Execute `app` configuration on an Emulator or Device (API 26+).

## Development Agents

This project uses the Android Native Agent Pack for AI-assisted development.
See [AGENTS.md](AGENTS.md) for details on how to use the available AI agents (`arch`, `ui`, `data`, etc.) to maintain code quality.

## Testing

Execute the full test suite via Gradle:
```bash
./gradlew test
```

## Project Context

See `context.md` for a high-level understanding of the architecture, data flows, and screen behavior.

## License

MIT License.
Developed by Cebola Studios.
