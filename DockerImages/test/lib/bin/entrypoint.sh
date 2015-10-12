#!/bin/bash
echo "Running entrypoint commands."
echo "pwd=`pwd`"
ls
cd ./test-app
bin/run.sh
