#!/bin/bash
export WHERE=$HOME
export CLASSPATH=.:$WHERE/DockerImages/HADS-Docker-Minimal/lib/RMI-1

cd $WHERE/DockerImages/HADS-Docker-Minimal/lib/RMI-1

mvn compile
mvn package
