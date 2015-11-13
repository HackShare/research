#!/bin/bash
WHAT="hads-""`date | sed 's/ /-/g' | sed 's/:/-/g'`".tgz
echo "WHAT = $WHAT"
cd $HOME/src
rm -f HADS/*.class HADS/*/*.class
tar cf - HADS | gzip -9 >/home/local/HADS/$WHAT
chmod u-w /home/local/HADS/$WHAT

