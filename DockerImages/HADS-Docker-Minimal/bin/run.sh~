#!/bin/bash
lib/HADS-Maven/bin/compile.sh | tee lib/HADS-Maven/bin/compilerOutput.txt
export err=$(cat lib/HADS-Maven/bin/compilerOutput.txt | grep "ERROR")
if [[ $err != *"ERROR"* ]]; then
	echo "Cleaning containers..."
	sleep 1s
	export testExists=$(docker ps -a | grep "HADS-Docker-Minimal"* | awk '{print $NF}')
	if [[ $testExists = "HADS-Docker-Minimal"* ]]; then	#A container is running
		echo "Stopping containers..."
		docker stop $(docker ps -a | grep "HADS-Docker-Minimal"* | awk '{print $NF}')
		echo "Removing stopped containers..."
		docker rm $(docker ps -a | grep "HADS-Docker-Minimal"* | awk '{print $NF}')
	else
		echo "No containers to clean."
	fi

	echo "Building image..."
	sleep 1s
	docker build -t="middle59/hads-docker-minimal" .
	
	echo "Creating Server containers..."
	for (( i=0; i<5; i++ ))
	do
		echo "Creating HADS-Docker-Minimal-Server$i."
		#docker run -d -it -P -e "CLASSPATH=.:/root/HADS-Maven/RMI-Maven-1.0-SNAPSHOT.jar" --name HADS-Docker-Minimal-Server$i middle59/hads-docker-minimal rmiregistry
		docker run -d -it -P -e "CLASSPATH=.:/root/HADS-Maven/HADS-Maven-1.0-SNAPSHOT.jar" --name HADS-Docker-Minimal-Server$i middle59/hads-docker-minimal rmiregistry
	done
	
	#docker cp ~/DockerImages/HADS-Docker-Minimal/lib/RMI-1/target/RMI-Maven-1.0-SNAPSHOT.jar HADS-Docker-Minimal-Server0:/root/HADS-Maven/RMI-Maven-1.0-SNAPSHOT.jar 
	#docker exec -d HADS-Docker-Minimal-Server0 java -cp .:/root/HADS-Maven/RMI-Maven-1.0-SNAPSHOT.jar HelloImpl
	#export ImplIP=$(docker inspect $(docker ps -a | grep "HADS-Docker-Minimal-Server0" | awk '{print $NF}') | grep IPAddress.: | awk {'print $2'} | cut -d '"' -f2)
	#perl -pi -w -e 's/'10.0.2.15'/'$ImplIP'/g;' ~/DockerImages/HADS-Docker-Minimal/lib/RMI-1/src/RMI/HelloClient.java
	#~/DockerImages/HADS-Docker-Minimal/lib/RMI-1/bin/compile.sh
	#docker cp ~/DockerImages/HADS-Docker-Minimal/lib/RMI-1/target/RMI-Maven-1.0-SNAPSHOT.jar HADS-Docker-Minimal-Server1:/root/HADS-Maven/RMI-Maven-1.0-SNAPSHOT.jar 
	#docker exec HADS-Docker-Minimal-Server1 java -cp .:/root/HADS-Maven/RMI-Maven-1.0-SNAPSHOT.jar HelloClient


	bin/buildXML.sh

	for (( i=0; i<5; i++ ))
	do
		echo "Adding XML to HADS-Docker-Minimal-Server$i."
		docker cp lib/HADS-Maven/src/main/java/HADS/XML/docker.xml HADS-Docker-Minimal-Server$i:/root/HADS-Maven/docker.xml

	done
		
	lib/HADS-Maven/bin/run.sh docker
else
	echo "There were ERRORS in the compile phase."
fi
