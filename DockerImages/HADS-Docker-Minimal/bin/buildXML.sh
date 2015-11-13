#!/bin/bash
pwd
cp lib/HADS-Maven/src/main/java/HADS/XML/local.xml lib/HADS-Maven/src/main/java/HADS/XML/docker.xml

export NameList='hadsA hadsB hadsC hadsD hadsE'
export IPList=$(sudo docker inspect $(sudo docker ps -a | grep "HADS-Docker-Minimal"* | awk '{print $NF}') | grep IPAddress.: | awk {'print $2'} | cut -d '"' -f2)

for (( i=1; i<6; i++ ))
do
	perl -pi -w -e 's/'$(echo $NameList | cut -d ' ' -f$i)'/'$(echo $IPList | cut -d " " -f	$i)'/g;' lib/HADS-Maven/src/main/java/HADS/XML/docker.xml
done



