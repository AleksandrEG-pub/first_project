#!/bin/bash

./start-database.sh

./gradlew :loggin:publishToMavenLocal --no-configuration-cache
./gradlew :database-connector:publishToMavenLocal --no-configuration-cache
./gradlew :audit:publishToMavenLocal --no-configuration-cache

#./gradlew shadowJar && java -jar ./app/build/libs/app-all.jar --file=application.env
