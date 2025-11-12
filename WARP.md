# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

Project overview
- Android app written in Kotlin using Jetpack Compose and MVVM.
- Single Gradle module: app.
- Layers:
  - data: Retrofit API client (OkHttp + Gson), DataStore-based UserPreferences, and DTO models.
  - presentation: Compose screens grouped by feature (login, jugador, arbitro, components) with ViewModels per feature and simple factories to inject an Android Context.
  - entry: MainActivity sets up Compose, reads persisted auth state from DataStore, and builds a Navigation Compose graph with role-based start destinations.
- Features of note:
  - QR scanner built with CameraX and ML Kit (Barcode Scanning) to drive registration flows.
  - Role-aware navigation: jugador (player) and arbitro (referee) home flows.

Key files and responsibilities
- app/src/main/java/com/example/sistemgestiondeportiva/MainActivity.kt: Sets theme and NavHost; chooses startDestination based on DataStore flags (isLoggedIn, rolID). Defines navigation to login, registro-* flows, jugador/*, arbitro/*, and QR scan.
- app/src/main/java/com/example/sistemgestiondeportiva/data/api/RetrofitClient.kt: Central Retrofit/OkHttp setup, logging interceptor, and BASE_URL.
- app/src/main/java/com/example/sistemgestiondeportiva/data/api/ApiService.kt: Typed Retrofit interface for auth, user, jugador, arbitro, and partido endpoints.
- app/src/main/java/com/example/sistemgestiondeportiva/data/local/UserPreferences.kt: DataStore-backed auth state (token, rolID, user, isLoggedIn, userID) with Flow accessors.
- app/src/main/java/com/example/sistemgestiondeportiva/presentation/**: Compose screens and ViewModels for login, jugador, arbitro, and shared components (e.g., QRScannerScreen).
- app/src/main/AndroidManifest.xml: Declares INTERNET and CAMERA, enables cleartext traffic (usesCleartextTraffic="true").

Environment and configuration
- API base URL is hardcoded for local/LAN development:
  - app/src/main/java/com/example/sistemgestiondeportiva/data/api/RetrofitClient.kt → BASE_URL = "http://192.168.137.1:5022/" (cleartext HTTP).
  - Manifest allows cleartext traffic; change BASE_URL to point at your backend and update manifest/network security config as needed for production.
- ViewModels depend on Android Context via simple factories (no DI container). When adding new ViewModels, mimic existing *ViewModelFactory patterns.

Build, install, and run
- Windows (PowerShell):
```powershell path=null start=null
# Build all variants
.\gradlew.bat build

# Assemble debug APK
.\gradlew.bat :app:assembleDebug

# Install debug on a connected/emulated device
.\gradlew.bat :app:installDebug
```
- macOS/Linux (bash):
```bash path=null start=null
# Build all variants
./gradlew build

# Assemble debug APK
./gradlew :app:assembleDebug

# Install debug on a connected/emulated device
./gradlew :app:installDebug
```

Lint and static checks
- Android Lint:
```powershell path=null start=null
# Windows
.\gradlew.bat :app:lint
```
```bash path=null start=null
# macOS/Linux
./gradlew :app:lint
```

Testing
- Unit tests (none present by default, but Gradle tasks are configured):
```powershell path=null start=null
# Windows: run all unit tests
.\gradlew.bat test
# Or debug unit tests for app
.\gradlew.bat :app:testDebugUnitTest

# Run a single unit test by fully-qualified name
.\gradlew.bat test --tests "com.example.package.ClassName.methodName"
```
```bash path=null start=null
# macOS/Linux equivalents
./gradlew test
./gradlew :app:testDebugUnitTest
./gradlew test --tests "com.example.package.ClassName.methodName"
```
- Instrumented tests (require device/emulator):
```powershell path=null start=null
.\gradlew.bat :app:connectedDebugAndroidTest
# Filter to a single class or method
.\gradlew.bat :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.package.ClassName#methodName
```

Architecture details to know before editing
- Navigation: NavHost routes are simple strings (e.g., "login", "scan-qr", "registro-...", "jugador/*", "arbitro/*"). Some routes carry path params (e.g., registro-jugador/{token}/{equipoID}) with Base64-encoded token to keep navigation-safe.
- State: ViewModels expose StateFlow for screen state; UI collects with collectAsState(). Network calls are made in viewModelScope; errors surface as strings via StateFlow or callbacks.
- Auth persistence: On successful auth or registration, LoginViewModel stores a Bearer token and user metadata in DataStore via UserPreferences; MainActivity reads DataStore to choose startDestination and supports logout by clearing DataStore.
- Networking: OkHttp logging is enabled at BODY level; additional in-app Log.d traces exist around QR and Retrofit interceptors to aid debugging. All API requests expect Authorization: Bearer <token> where needed.
- QR scanning: CameraX Preview + ImageAnalysis feeds ML Kit barcode scanner; first detected QR is sent upstream and screen shows a transient progress indicator while validating the QR via backend.

Gradle and toolchain
- Kotlin DSL build (build.gradle.kts), Gradle 8.13, Android Gradle Plugin 8.13.0, Kotlin 2.0.21. Compose is enabled; Java/Kotlin target 11.

When extending the app
- Add new endpoints to ApiService, then call from a ViewModel using RetrofitClient.apiService and persist/consume any auth via UserPreferences.
- Add new screens under presentation/<feature> and hook into the NavHost in MainActivity; if the screen needs data, create a ViewModel and a matching *ViewModelFactory(context).
- If you need environment-specific backends, consider moving BASE_URL to BuildConfig fields per buildType and avoid hardcoding.
