#!/bin/sh

./start-database.sh
./gradlew shadowJar && java -jar ./app/build/libs/app-all.jar --file=application.env
