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

# sh -c "./runsLocal.sh </dev/null >runsLocal.out 2>&1 &"

bin/runLocalServersHADS.sh run3ServersLocal0200T
bin/runLocalServersHADS.sh run4ServersLocal0200T
bin/runLocalServersHADS.sh run5ServersLocal0200T
bin/runLocalServersHADS.sh run6ServersLocal0200T
bin/runLocalServersHADS.sh run7ServersLocal0200T

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocal0200F
bin/runLocalServersHADS.sh run4ServersLocal0200F
bin/runLocalServersHADS.sh run5ServersLocal0200F
bin/runLocalServersHADS.sh run6ServersLocal0200F
bin/runLocalServersHADS.sh run7ServersLocal0200F

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocal1000T
bin/runLocalServersHADS.sh run4ServersLocal1000T
bin/runLocalServersHADS.sh run5ServersLocal1000T
bin/runLocalServersHADS.sh run6ServersLocal1000T
bin/runLocalServersHADS.sh run7ServersLocal1000T

bin/cleanLocalServers.sh
bin/compileLocalServers.sh
sleep 60

bin/runLocalServersHADS.sh run3ServersLocal1000F
bin/runLocalServersHADS.sh run4ServersLocal1000F
bin/runLocalServersHADS.sh run5ServersLocal1000F
bin/runLocalServersHADS.sh run6ServersLocal1000F
bin/runLocalServersHADS.sh run7ServersLocal1000F

#############################################################################
bin/cleanLocalServers.sh

