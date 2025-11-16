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

2. Verify Java is installed and `JAVA_HOME` is set. Expected 17. Example:

```
java -version
echo $JAVA_HOME
```

## How to launch
- Run using start.sh or start-in-memory.sh scripts:

```bash
# Run with database repository
start-with-database.sh

# Run with in-memory repository
start-in-memory.sh

# Run with in-memory repository
start-with-file.sh
```

## Features
- Console-based UI (CLI) with menu-driven interactions.
- File-based persistence of users, products, and audit logs by default (stored in project working directory / `data/` files) Option: --repository-type=file 
- In-memory mode available for ephemeral runs (pass `--repository-type=in-memory`).
- Database persistence available with option: `--repository-type=database`
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
Not authenticated users can not perform any actions, except log in attempts


## Restrictions and known limitations
- Default storage uses the project working directory. This is not production-safe: redeploying or cleaning the workspace may delete runtime data.
- Concurrent access by multiple instances is not supported and may corrupt file-based storage.
- Default admin/user passwords are only for convenience in demos — treat them as insecure for production.


## Where to look in the code
- Main entry: `app/src/main/java/org/example/App.java`
- App configuration and startup: `app/src/main/java/org/example/configuration/ApplicationConfiguration.java`
- Data initializer: `app/src/main/java/org/example/util/DataInitializer.java`
- Liquibase configuration: `app/src/main/java/org/example/configuration/LiquibaseConfiguration.java`
- Service wiring: `app/src/main/java/org/example/configuration/ServiceConfiguration.java`
- Repositories and services: `app/src/main/java/org/example/repository` and `.../service`

## Troubleshooting
- Permission errors: ensure the executing user can read/write the project directory.
- Missing/duplicate data on initialization: check the `data/` files in the project root (e.g. `users.csv`, `products.csv`, `audit.csv`).
