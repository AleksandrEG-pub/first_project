#!/bin/bash

gradle shadowJar && java -jar ./app/build/libs/app-all.jar
