# Weather (Compose + Material 3 + Open-Meteo)

Build: `export JAVA_HOME=<jdk17> ANDROID_HOME=<sdk>`; `./gradlew assembleDebug` (needs `local.properties` with `sdk.dir=...` or ANDROID_HOME).
Packages: `data` (Retrofit/DTO/repository impl), `domain` (models, repository interface, use cases), `ui` (Compose screens, ViewModels).
