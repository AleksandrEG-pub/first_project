#!/bin/bash

./start-database.sh
./gradlew shadowJar && java -jar ./app/build/libs/app-all.jar --repository-type=database --file=application.env
