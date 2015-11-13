#!/bin/bash
touch listing; for i in */*.java *.java; do echo "========= $i ===========" >>listing; echo " " >>listing; cat $i >> listing; echo " " >>listing; done
pr -o2 -t -l10000 listing | pr -t -n:5 -l10000 >listing2
enscript -pout.ps -fCourier11 listing2
