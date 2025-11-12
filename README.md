# Giphy Test Assignment

This is an Android application for searching and browsing GIFs using the Giphy API. 
It is built with Jetpack Compose, following Clean Architecture principles and modern Android development best practices.

## Features

### Primary Features
- **Auto Search**: Search requests are performed automatically after a 500ms delay when the user stops typing
- **Pagination**: Automatic loading of more results when scrolling (handled by Giphy SDK)
- **Error Handling**: Multi-layer error handling with user-friendly error messages
- **Grid Display**: Results displayed in a responsive grid layout (2-3 columns based on screen size)
- **Details Screen**: Tap any GIF to view detailed information

### Additional Features
- **Favorites System**: Save and manage favorite GIFs locally
- **Trending Searches**: Discover popular search terms with filter chips
- **Autocomplete Suggestions**: Smart search suggestions as you type
- **Theme Support**: Light, Dark, and System theme options
- **Settings Screen**: User preferences management
- **Share Functionality**: Share GIF URLs via Android share intent
- **Offline Detection**: Network availability monitoring with offline indicators
- **State Persistence**: Search queries preserved across configuration changes

## Architecture

The project follows **MVI (Model-View-Intent) + Clean Architecture** pattern:

- **UI Layer**: Jetpack Compose screens, ViewModels, State/Event/Effect pattern
- **Domain Layer**: UseCases, Domain Models, Repository Interfaces
- **Data Layer**: Repository implementations, Data Sources (API, Room Database, Proto DataStore)

### Key Principles
- Separation of concerns
- Dependency inversion
- Single responsibility
- Testability

## Technologies & Libraries

### Core
- **Kotlin**: 100% Kotlin codebase
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components
- **Navigation Compose**: Type-safe navigation

### Architecture Components
- **ViewModel**: State management and business logic
- **StateFlow**: Reactive data streams
- **Room**: Local database for favorites
- **Proto DataStore**: Type-safe data storage for user settings

### Dependency Injection
- **Hilt**: Dependency injection framework

### Networking
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: HTTP client with logging interceptor
- **Giphy SDK**: Official Giphy Android SDK for grid display

### Asynchronous Programming
- **Kotlin Coroutines**: Asynchronous programming
- **Flow**: Reactive streams

### Testing
- **JUnit**: Unit testing framework
- **MockK**: Mocking library for Kotlin
- **Turbine**: Flow testing
- **Coroutines Test**: Testing coroutines
- **Robolectric**: Android unit testing

## Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 11 or higher
- **Android SDK**: API 24 (Android 7.0) minimum, API 36 target
- **Gradle**: 8.0 or higher

## Setup Instructions

### 1. Clone the Repository
```bash
git clone [repository-url]
cd GIPHY_Test_Assignment
```

### 2. Configure Giphy API Key

**IMPORTANT**: The app requires a Giphy API key to function properly.

1. Get your Giphy API key from [Giphy Developers](https://developers.giphy.com/)
2. Create a `local.properties` file in the root directory (if it doesn't exist)
3. Add the following line to `local.properties`:
   ```properties
   giphy.api.key=YOUR_API_KEY_HERE
   ```

### 3. Build and Run

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator (API 24+)
4. Click "Run" or press `Shift+F10`

### 4. Build from Command Line

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/chililabs/giphytest/
│   │   │   ├── data/              # Data layer
│   │   │   │   ├── local/         # Room database, DataStore
│   │   │   │   ├── remote/        # API models, Retrofit
│   │   │   │   └── repo/          # Repository implementations
│   │   │   ├── domain/            # Domain layer
│   │   │   │   ├── model/         # Domain models
│   │   │   │   ├── repo/          # Repository interfaces
│   │   │   │   └── usecase/       # Business logic use cases
│   │   │   ├── ui/                # UI layer
│   │   │   │   ├── screens/       # Screen composables
│   │   │   │   ├── main/          # Main activity, navigation
│   │   │   │   ├── theme/         # Theme, dimensions
│   │   │   │   └── base/          # Base ViewModel
│   │   │   ├── di/                # Dependency injection modules
│   │   │   └── utils/             # Utilities, extensions
│   │   ├── proto/                 # Proto DataStore schemas
│   │   └── res/                   # Resources (strings, themes)
│   └── test/                      # Unit tests
│       └── java/com/chililabs/giphytest/
buildSrc/                          # Build utilities (versioning, config)
```

## Testing

The project includes unit tests covering:

- **ViewModels**: State management and event handling
- **UseCases**: Business logic validation
- **Repositories**: Data layer operations

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run with coverage
./gradlew testDebugUnitTest --continue
```

### Test Coverage
- 17 test files
- Test coverage for ViewModels, UseCases and Repositories
- Uses MockK for mocking, Turbine for Flow testing

### Main Screens
- **Search Screen**: Grid of trending/search results with search field
- **Details Screen**: Detailed GIF view with metadata and actions
- **Favorites Screen**: Grid of saved favorite GIFs
- **Settings Screen**: Theme preferences (might be updated with more setting option in the future)

## Configuration

### Build Configuration
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36
- **Version**: Defined in `gradle.properties`

### Semantic Versioning
The project uses semantic versioning with automatic version code generation. See `buildSrc/src/main/kotlin/Versioning.kt` for details.

## License

This project is created as part of a job application assignment.

## Author

Maksym Kravchenko

## Acknowledgments

- [Giphy](https://giphy.com/) for providing the API and SDK
- Android team for Jetpack Compose and modern Android libraries