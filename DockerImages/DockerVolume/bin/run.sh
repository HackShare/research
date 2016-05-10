#!/bin/bash
#This script is bare minimum to sample run a container
#I have named the image for ../Dockerfile as "vt1"

#To adda volume to the container you must specify in this notation
#-v (Absolute filepath):(desired container path)
#Running as daemon remain open
docker run -dit --name vt1 -v /home/mike/DockerImages/DockerVolume/src:/srcImpl vt1 /bin/bash
echo "Contents of Volume ./srcImpl:"
docker exec vt1 ls srcImpl
