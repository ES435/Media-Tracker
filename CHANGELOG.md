# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased] Finished version

### Added

- 


### Changed

- 


### Removed

- 


## [0.1.1] - 2026-01-18

### Added

- Centralized configuration package (`app.mediatracker.config`) for Mongo and WebClient settings.
- `ManualEntryCommand` pattern in LibraryService to handle complex input parameters cleanly.

### Changed

- **Architecture:** Refactored backend structure to "Package by Feature" (moved search logic to `feature.search`).
- **Refactoring:** Decoupled Authentication logic from `UserService` into a dedicated `AuthService`.
- **Clean Code:** Renamed cryptic variables (e.g., 'q', 'n') in Search Providers to descriptive names for better readability.
- **Security:** Externalized hardcoded JWT secret key to `application.yml`.

### Fixed

- Critical `NullPointerException` in `JwtFilter` when requests contained no cookies.
- Security vulnerability where sensitive API keys were logged to the console in `RawgClient`.
- CORS configuration issues: Now allows PATCH, DELETE, and OPTIONS requests from the frontend.

### Removed

- Redundant `CorsConfig` class (functionality moved to `SecurityConfig`).
- Obsolete static HTML prototype files (`tech.html`, `lists.html`, `index.html`).
- Unused imports across the backend codebase.

## [0.1.0] - 2026-01-12

### Added

- Frontend Search and Login Page
- User Search and User Page Endpoints
- User Login and Registration Endpoints
- Test Data JSONs and Test Data Seeder

### Fixed

- 

### Changed

- 

### Removed

-