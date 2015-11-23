#!/bin/bash
echo "Cleaning containers..."
sleep 1s

export testExists=$(docker ps -a | grep "RMI-Example"* | awk '{print $NF}')
if [[ $testExists = "RMI-Example"* ]]; then	#A container is running
	echo "Stopping containers..."
	docker stop $(docker ps -a | grep "RMI-Example"* | awk '{print $NF}')
	echo "Removing stopped containers..."
	docker rm $(docker ps -a | grep "RMI-Example"* | awk '{print $NF}')
else
	echo "No containers to clean."
fi

echo "Building image..."
sleep 1s
docker build -t="middle59/rmi-example" .

echo "Creating RMI-Example-Impl container..."
sleep 1s

export currName="RMI-Example-Impl"

#docker run -d -it --name $currName -v /src/RMI-1/*:/root/RMI-Example middle59/rmi-example rmiregistry
docker run -d -it --name $currName middle59/rmi-example rmiregistry
docker exec -d $currName sh entrypoint.sh HelloImpl

docker logs $currName

#EXPORT Implementation Container's IP
export ImplIP=$(docker inspect $(docker ps -a | grep "RMI-Example-Impl" | awk '{print $NF}') | grep -m 1 IPAddress.: | awk {'print $2'} | cut -d '"' -f2)

echo "Creating RMI-Example-Client container..."
sleep 1s

export currName="RMI-Example-Client"

#docker run -d -it --name $currName -v /src/RMI-1/*:/root/RMI-Example middle59/rmi-example
docker run -d -it --name $currName middle59/rmi-example
#Add Implementation Container's IP to Client
docker exec $currName perl -pi -w -e 's/'10.0.2.15'/'$ImplIP'/g;' HelloClient.java
docker exec $currName sh entrypoint.sh HelloClient

#Done
