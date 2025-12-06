#!/bin/bash

./start-database.sh

./gradlew :app:bootJar
java -jar ./app/build/libs/app-1.0.0.jar
