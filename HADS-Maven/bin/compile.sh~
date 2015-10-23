#!/bin/bash
export WHERE=$HOME
export CLASSPATH=.:$WHERE/HADS-Maven

cd $WHERE/HADS-Maven

mvn compile
mvn package

pwd

for i in 10.0.2.15; do
   echo $i
   scp target/HADS-Maven-1.0-SNAPSHOT.jar $i:HADS/
   sleep 3
   echo $i
   ssh $i 'CLASSPATH=.:$HOME rmiregistry </dev/null 2>/dev/null 1>/dev/null &'
done
