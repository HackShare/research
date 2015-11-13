#!/bin/bash
export curUser=$(whoami)
if [ $curUser = "root" ]; then

	lib/HADS-Maven/bin/compile.sh | tee lib/HADS-Maven/bin/compilerOutput.txt
	export err=$(cat lib/HADS-Maven/bin/compilerOutput.txt | grep "ERROR")
	if [[ $err != *"ERROR"* ]]; then
		echo "Cleaning containers..."
		sleep 1s
		export testExists=$(sudo docker ps -a | grep "HADS-Docker-Minimal"* | awk '{print $NF}')
		if [[ $testExists = "HADS-Docker-Minimal"* ]]; then	#A container is running
			echo "Stopping containers..."
			docker stop $(sudo docker ps -a | grep "HADS-Docker-Minimal"* | awk '{print $NF}')
			echo "Removing stopped containers..."
			docker rm $(sudo docker ps -a | grep "HADS-Docker-Minimal"* | awk '{print $NF}')
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
			docker run -d -it -P --name HADS-Docker-Minimal-Server$i middle59/hads-docker-minimal /bin/bash
			docker exec HADS-Docker-Minimal-Server$i rmiregistry &
		done

		bin/buildXML.sh

		for (( i=0; i<5; i++ ))
		do
			echo "Adding XML to HADS-Docker-Minimal-Server$i."
			docker cp lib/HADS-Maven/src/main/java/HADS/XML/docker.xml HADS-Docker-Minimal-Server$i:/root/HADS-Maven/docker.xml
		done

		#sudo docker exec -it HADS-Docker-Minimal-Server0 java -cp ./root/HADS-Maven:./root/HADS-Maven/HADS-Maven-1.0-SNAPSHOT.jar HADS.Server.ServerImpl XML/local.xml

		lib/HADS-Maven/bin/run.sh docker
	else
		echo "There were ERRORS in the compile phase."
	fi
else
	echo "Not logged in as root. Try sudo -s"
fi
