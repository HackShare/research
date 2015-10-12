# Version: 0.0.1
FROM ubuntu:14.04
MAINTAINER Mike Middleton

RUN apt-get update
RUN apt-get install -y openjdk-7-jre
ADD lib/test-app ./test-app
ADD lib/bin ./bin
ENTRYPOINT ["bin/entrypoint.sh"]
