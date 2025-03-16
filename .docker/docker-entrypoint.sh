#!/bin/sh

# install maven on alpine
apk update && apk add maven

# Run mvn clean in /app
cd /app
mvn clean package

java -jar target/*.jar