#!/bin/bash
export HOMEDIR=test-app
cd $HOME/$HOMEDIR
echo "pwd=`pwd`"

java -jar target/test-app-1.0-SNAPSHOT.jar
