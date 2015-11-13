#!/bin/bash
export CLASSPATH=.:$HOME/src

cd $HOME/src

rm -f HADS/*.class HADS/*/*.class

cd HADS/XML
javac *.java
cd ../..

cd HADS/Server
javac *.java
cd ../..

cd HADS/Generator
javac *.java
cd ../..

# Is this necessary?  According to the tutorial, it is not.
# Sure enough, it works without this!
#rmic HADS.Server.ServerImpl

cd HADS
javac runHADS.java
cd ..
