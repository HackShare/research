#!/bin/bash

# Stop all running machines
docker-machine ls | grep -v Stopped | grep -v Error |  grep -v default | grep -v NAME | awk '{print $1}' | xargs --no-run-if-empty docker-machine stop

# Remove exited machines
docker-machine ls | grep -v Running | grep -v NAME | awk '{print $1}' | xargs --no-run-if-empty docker-machine rm

docker-machine ls
