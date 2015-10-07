#date
#echo "sleeping for 54000 seconds (15 hours)"
#sleep 54000
#echo "sleep done"
#date
#############################################################################
bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60
#############################################################################

# sh -c "./runsLocalO.sh </dev/null >runsLocalO.out 2>&1 &"

bin/runLocalServersHADS.sh run3ServersLocalO0200T
bin/runLocalServersHADS.sh run4ServersLocalO0200T
bin/runLocalServersHADS.sh run5ServersLocalO0200T
bin/runLocalServersHADS.sh run6ServersLocalO0200T
bin/runLocalServersHADS.sh run7ServersLocalO0200T

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocalO0200F
bin/runLocalServersHADS.sh run4ServersLocalO0200F
bin/runLocalServersHADS.sh run5ServersLocalO0200F
bin/runLocalServersHADS.sh run6ServersLocalO0200F
bin/runLocalServersHADS.sh run7ServersLocalO0200F

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocalO1000T
bin/runLocalServersHADS.sh run4ServersLocalO1000T
bin/runLocalServersHADS.sh run5ServersLocalO1000T
bin/runLocalServersHADS.sh run6ServersLocalO1000T
bin/runLocalServersHADS.sh run7ServersLocalO1000T

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocalO1000F
bin/runLocalServersHADS.sh run4ServersLocalO1000F
bin/runLocalServersHADS.sh run5ServersLocalO1000F
bin/runLocalServersHADS.sh run6ServersLocalO1000F
bin/runLocalServersHADS.sh run7ServersLocalO1000F

#############################################################################
bin/cleanLocalServers.sh

