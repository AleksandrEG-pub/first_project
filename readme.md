# First Project

## Overview
A small Java console application demonstrating file-based persistence, 
basic user/product management, and a simple CLI-driven UI. 
The project uses Gradle (Kotlin DSL) and stores runtime data in files under the project working directory by default.

## Requirements
- Java JDK 17
- Gradle Wrapper (use `./gradlew` included in the repo).
- Read/write permissions for the project directory (the app saves files to the working dir by default).

## Setup
1. Clone the repository and change into the project root:

```
git clone <repo-url>
cd <repo-root>
```

2. Verify Java is installed and `JAVA_HOME` is set. Example:

```
java -version
echo $JAVA_HOME
```

3. Build the project using the Gradle wrapper:

```bash
./gradlew clean build
```

4. (Optional) Run with in-memory mode to avoid file persistence (useful for tests):

- Pass `--in-memory=true` as a JVM program argument when launching the app.

## How to launch
- Run using the Gradle application plugin (if configured) or execute the produced JAR:

```bash
# Run with Gradle (preferred during development)
./gradlew run

# Run the packaged JAR after build
java -jar app/build/libs/app.jar

# Run with in-memory flag
./gradlew run --args='--in-memory=true'
```

## Features
- Console-based UI (CLI) with menu-driven interactions.
- File-based persistence of users, products, and audit logs by default (stored in project working directory / `data/` files).
- In-memory mode available for ephemeral runs (pass `--in-memory=true`).
- Simple authentication and authorization (roles: ADMIN, USER).
- Product management (add, list, search) and basic audit logging.

## Present initialization data
On first run (or when storage is empty), the application initializes default data automatically. 
Present initial data added by the initializer includes:
- Users:
  - admin (role ADMIN) with password hashed from `Admin123!`
  - user (role USER) with password hashed from `User123!`
- Sample products (electronics, furniture, sports, appliances), e.g. "Laptop Pro 15", "Wireless Mouse", "Office Chair", etc.

The initializer is implemented in `app/src/main/java/org/example/util/DataInitializer.java` and runs from `ApplicationConfiguration.initializeData()`.

## Important notes on users
Application does not support creation of custom users. 
Not authenticated users can not log in and perform any actions.


## Restrictions and known limitations
- Default storage uses the project working directory. This is not production-safe: redeploying or cleaning the workspace may delete runtime data.
- Concurrent access by multiple instances is not supported and may corrupt file-based storage.
- Default admin/user passwords are only for convenience in demos — treat them as insecure for production.


## Where to look in the code
- Main entry: `app/src/main/java/org/example/App.java`
- App configuration and startup: `app/src/main/java/org/example/configuration/ApplicationConfiguration.java`
- Data initializer: `app/src/main/java/org/example/util/DataInitializer.java`
- Service wiring: `app/src/main/java/org/example/configuration/ServiceConfiguration.java`
- Repositories and services: `app/src/main/java/org/example/repository` and `.../service`

## Troubleshooting
- Permission errors: ensure the executing user can read/write the project directory.
- Missing/duplicate data on initialization: check the `data/` files in the project root (e.g. `users.csv`, `products.csv`, `audit.csv`).
