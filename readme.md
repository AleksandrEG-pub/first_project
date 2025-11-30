# First Project

## Overview
A Java spring http server (tomcat) application with postgres persistence, 
basic user/product management

## Requirements
- Java JDK 17
- Gradle Wrapper (use `./gradlew` included in the repo)
- Docker - start will require to have or download postgresql image

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
- Run using start.sh. This will launch docker

```bash
# Run the application
start.sh
```

## Features
- http-driven interactions
- SQL postgres persistence of users, products, and audit logs 
- Simple authentication and authorization (roles: ADMIN, USER).
- Product management (add, list, search) and basic audit logging.
- configuration for application located in ./app/src/main/resources/application.yaml 

## Present initialization data
On first run (or when storage is empty), the application initializes default data automatically. 
Present initial data added by the initializer includes:
- Users:
  - admin (role ADMIN) with password hashed from `Admin123!`
  - user (role USER) with password hashed from `User123!`
- Sample products (electronics, furniture, sports, appliances), e.g. "Laptop Pro 15", "Wireless Mouse", "Office Chair", etc.


## Important notes on users
Application does not support creation of custom users. 
Not authenticated users can not perform any actions, except log in attempts


## Restrictions and known limitations
- Default admin/user passwords are only for convenience in demos — treat them as insecure for production.


## Where to look in the code
- Main entry: `app/src/main/java/org/example/App.java`
- App configuration and startup: `app/src/main/java/org/example/configuration/ApplicationConfiguration.java`
- Liquibase configuration: `app/src/main/java/org/example/configuration/LiquibaseConfiguration.java`
- Repositories and services: `app/src/main/java/org/example/repository` and `.../service`


## API
API can be discovered by swagger-ui after application start by address:

```
http://localhost:8094/swagger-ui/index.html
```

Note that swagger UI accessible only for admin users.

login: admin, password: Admin123!
