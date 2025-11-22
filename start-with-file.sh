#!/bin/bash

./gradlew shadowJar && java -jar ./app/build/libs/app-all.jar --repository-type=file
