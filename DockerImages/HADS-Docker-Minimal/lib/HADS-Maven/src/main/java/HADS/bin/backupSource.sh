#!/bin/bash
if [ X$1 = "X" ]; then
   WHERE=zeus.stockton.edu; 
else
   WHERE=$1
fi
if [ X$2 = "X" ]; then
   WHO=hads
else
   WHO=$2
fi
echo "WHERE = $WHERE"
echo "WHO = $WHO"
WHAT="hads-""`date | sed 's/ /-/g' | sed 's/:/-/g'`".tgz
echo "WHAT = $WHAT"
cd $HOME/src/HADS
tar cf - bin Generator/*.java Misc *.java *.sh Server/*.java SSH XML |\
  gzip -9 | ssh $WHO@$WHERE "cat >$WHAT; chmod u-w $WHAT"

