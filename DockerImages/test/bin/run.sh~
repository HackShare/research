#!/bin/bash
export curUser=$(whoami)
if [ $curUser = "root" ]; then
	echo "Cleaning containers..."
	sleep 1s
	export testExists=$(docker ps -a | grep -w -o "test")
	if [[ $testExists = "test" ]]; then
		echo "Stopping container..."
		docker stop test
		echo "Removing container..."
		docker rm test
	else
		echo "No container to clean."
	fi

	echo "Building image..."
	sleep 1s
	docker build -t="middle59/testubuntu" .

	echo "Creating container..."
	sleep 1s
	docker run -d --name test middle59/testubuntu

	echo "Monitoring logs.."
	sleep 1s
	docker logs -f test
else
	echo "Not logged in as root. Try sudo -s"
fi
