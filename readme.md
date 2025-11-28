# First Project

## Overview
A small Java http server application with postgres persistence, 
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
- htto-driven interactions
- SQL postgres persistence of users, products, and audit logs 
- Simple authentication and authorization (roles: ADMIN, USER).
- Product management (add, list, search) and basic audit logging.
- configuration for database and migrations passed using file, which supplied to application as an argument --file=/path/to/file. By default it is a file application.env in project root. 

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
- Default storage uses the project working directory. This is not production-safe: redeploying or cleaning the workspace may delete runtime data.
- Concurrent access by multiple instances is not supported and may corrupt file-based storage.
- Default admin/user passwords are only for convenience in demos — treat them as insecure for production.


## Where to look in the code
- Main entry: `app/src/main/java/org/example/App.java`
- App configuration and startup: `app/src/main/java/org/example/configuration/ApplicationConfiguration.java`
- Liquibase configuration: `app/src/main/java/org/example/configuration/LiquibaseConfiguration.java`
- Service wiring: `app/src/main/java/org/example/configuration/ServiceConfiguration.java`
- Repositories and services: `app/src/main/java/org/example/repository` and `.../service`

## API
- GET /products    
- GET /products?id=1&name={name}&category={category}&brand={brand}&minPrice={0.05}&maxPrice={19.99}
- GET /products/{id}
- POST /products/{id}, body ProductForm
- PUT /products/{id},  body ProductForm
- DELETE /products/{id}  
- GET /audits    
- GET /audits?username={user}  

## API objects examples:
ProductForm:
```
{
  "name": "Wireless Bluetooth Headphones",
  "description": "High-quality wireless headphones with noise cancellation",
  "category": "ElectronicsElectronics",
  "brand": "TechAudio",
  "price": 129.99
}
```

## API security
user 'user' authorized only for:
- GET /products

user 'admin' authorized for all APIs