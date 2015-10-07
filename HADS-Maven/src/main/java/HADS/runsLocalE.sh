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

# sh -c "./runsLocalE.sh </dev/null >runsLocalE.out 2>&1 &"

bin/runLocalServersHADS.sh run3ServersLocalE0200T
bin/runLocalServersHADS.sh run4ServersLocalE0200T
bin/runLocalServersHADS.sh run5ServersLocalE0200T
bin/runLocalServersHADS.sh run6ServersLocalE0200T
bin/runLocalServersHADS.sh run7ServersLocalE0200T

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocalE0200F
bin/runLocalServersHADS.sh run4ServersLocalE0200F
bin/runLocalServersHADS.sh run5ServersLocalE0200F
bin/runLocalServersHADS.sh run6ServersLocalE0200F
bin/runLocalServersHADS.sh run7ServersLocalE0200F

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocalE1000T
bin/runLocalServersHADS.sh run4ServersLocalE1000T
bin/runLocalServersHADS.sh run5ServersLocalE1000T
bin/runLocalServersHADS.sh run6ServersLocalE1000T
bin/runLocalServersHADS.sh run7ServersLocalE1000T

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocalE1000F
bin/runLocalServersHADS.sh run4ServersLocalE1000F
bin/runLocalServersHADS.sh run5ServersLocalE1000F
bin/runLocalServersHADS.sh run6ServersLocalE1000F
bin/runLocalServersHADS.sh run7ServersLocalE1000F

#############################################################################
bin/cleanLocalServers.sh

