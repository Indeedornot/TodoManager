#!/bin/sh

# install maven on alpine
apk update && apk add maven

# Run mvn clean in /app
cd /app
mvn package

java -jar target/*.jar