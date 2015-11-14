# Version: 0.0.1
FROM ubuntu:14.04
MAINTAINER Mike Middleton

RUN apt-get update
RUN apt-get install -y openjdk-7-jre
RUN apt-get update
RUN apt-get install -y openjdk-7-jdk
RUN apt-get install -y ufw

ADD src/RMI-1 root/RMI-Example/
#VOLUME /root/RMI-Example

WORKDIR /root/RMI-Example
