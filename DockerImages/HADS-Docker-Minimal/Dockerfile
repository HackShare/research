# Version: 0.0.1
FROM ubuntu:14.04
MAINTAINER Mike Middleton

RUN apt-get update
RUN apt-get install -y openjdk-7-jre
RUN apt-get install -y openjdk-7-jdk
RUN apt-get install -y ufw
RUN apt-get install -y nano
ADD lib/HADS-Maven/target/HADS-Maven-1.0-SNAPSHOT.jar ./root/HADS-Maven/HADS-Maven-1.0-SNAPSHOT.jar

WORKDIR root/HADS-Maven

#EXPOSE 1099
