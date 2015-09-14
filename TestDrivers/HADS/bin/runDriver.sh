#!/bin/bash
cd $HOME/TestDrivers/HADS/
echo "pwd=`pwd`"
CLASSPATH=.:$HOME/TestDrivers:$HOME/hazelcast-3.5.2/lib/hazelcast-3.5.2.jar java Driver
