# Research
Research Code -- Including distributed servers, in-memory grid systems, and containers

##**Directories**

**./TestDrivers/HADS**

Testing of Hazelcast's map operations.
  
  Includes: Driver, Hazelcast Storage

**./test-app**

Maven built project testing the basic distributed data structures of Hazelcast, Redisson, and Gridgain.
  
  Includes: Driver, Hazelcast/Redis/Gridgain Storage, Maven POM

**./HADS-Maven**

Integration between COPAR* and 3 different in-memory grid storage systems including Hazelcast, Redisson, and Gridgain.
  
  Includes: Hazelcast/Redis/Gridgain Storage, Maven POM, COPAR Simulation
  
  *Information about Rowan University's COPAR simulator can be found at: http://elvis.rowan.edu/~crichlow/coparciit04a.pdf
    Additional research works can be found on my website: http://elvis.rowan.edu/~middle59/websites/MikeMiddleton/research/links/

**./DockerImages/test**

Combines the previous test-app and adds an additional layer including Docker so we may run the application in a container.
  
  Includes: ./test-app, Dockerfile, additional shell files
