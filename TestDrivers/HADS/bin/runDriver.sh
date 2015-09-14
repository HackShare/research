#!/bin/bash
cd $HOME/test/HADS/
echo "pwd=`pwd`"
CLASSPATH=.:$HOME/test:$HOME/hazelcast-3.5.2/lib/hazelcast-3.5.2.jar java Driver
