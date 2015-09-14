#!/bin/bash
export WHERE=$HOME
export CLASSPATH=.:$WHERE/TestDrivers:$WHERE/hazelcast-3.5.2/lib/hazelcast-3.5.2.jar

cd $WHERE/test

cd HADS/Server
javac *.java
cd ../..

cd HADS
javac Driver.java
cd ../..
