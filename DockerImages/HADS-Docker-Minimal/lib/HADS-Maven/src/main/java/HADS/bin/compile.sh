#!/bin/bash
export WHERE=$HOME
export CLASSPATH=.:$WHERE/src

cd $WHERE/src

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

for i in 10.0.2.15; do
   echo $i
   tar cf - HADS/*/*.class | ssh $i 'cd $HOME; tar xpf -; ls -al HADS; du -s HADS'
   sleep 3
   echo $i
   ssh $i 'CLASSPATH=.:$HOME rmiregistry </dev/null 2>/dev/null 1>/dev/null &'
done
