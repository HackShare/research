#!/bin/bash
if [ X$1 = "X" ]; then echo "no XML file name argument"; exit; fi
cd $HOME/DockerImages/HADS-Docker-Minimal/lib/HADS-Maven/src/main/java/HADS
echo "pwd=`pwd`"
if [ ! -f XML/$1.xml ]; then echo "XML file does not exist"; exit; fi
echo "input = XML/$1.xml"
PID=$$
echo "PID = $PID"
FILE=file$PID.xml
echo "FILE = $FILE"
cp XML/$1.xml XML/$FILE

pwd

time java -cp ../../../../target/HADS-Maven-1.0-SNAPSHOT.jar RunHADS XML/$FILE

echo "rm XML/$FILE"
rm -f XML/$FILE
