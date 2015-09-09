#!/bin/bash

echo "Build"
mvn clean package

echo "*****     TASK C     *****"
storm jar target/storm-example-0.0.1-SNAPSHOT.jar TopWordFinderTopologyPartC data.txt > output-part-c.txt

echo "*****     TASK D     *****"
storm jar target/storm-example-0.0.1-SNAPSHOT.jar TopWordFinderTopologyPartD data.txt > output-part-d.txt
