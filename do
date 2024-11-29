#!/bin/bash
mvn clean package -DskipTests
java -jar target/cnsim-0.0.1-SNAPSHOT.jar -c src/main/resources/bitcoin-config/bitcoin.application.properties
