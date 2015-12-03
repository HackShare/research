#!/bin/bash
if [ X$1 = "X" ]; then echo "No java entrypoint specified -- try: HelloImpl, HelloClient"; exit; fi
javac -cp . *.java
java -cp . $1
