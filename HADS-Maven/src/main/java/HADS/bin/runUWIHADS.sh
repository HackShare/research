#!/bin/bash
if [ X$1 = "X" ]; then echo "no XML file name argument"; exit; fi
cd $HOME/src/HADS
echo "pwd=`pwd`"
if [ ! -f XML/$1.xml ]; then echo "XML file does not exist"; exit; fi
echo "input = XML/$1.xml"
PID=$$
echo "PID = $PID"
FILE=file$PID.xml
echo "FILE = $FILE"
cp XML/$1.xml XML/$FILE
for i in antonio boerne poteet schertz austin dallas houston 64.28.143.30; do scp XML/$1.xml $i:HADS/XML/$FILE; done
for i in antonio boerne poteet schertz austin dallas houston 64.28.143.30; do ssh $i ls -l HADS/XML/$FILE; done
echo "CLASSPATH=.:$HOME/src java RunHADS XML/$FILE"
CLASSPATH=.:$HOME/src java RunHADS XML/$FILE
echo "rm XML/$FILE"
rm -f XML/$FILE
