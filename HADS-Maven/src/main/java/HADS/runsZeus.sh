date
echo "sleeping for 46800 seconds (13 hours)"
sleep 46800
echo "sleep done"
date
#############################################################################
bin/clean.sh
bin/compile.sh
sleep 60
#############################################################################

# sh -c "./runsZeus.sh </dev/null >runsZeus.out 2>&1 &"

bin/runHADS.sh run3ServersZeus0200F
bin/runHADS.sh run3ServersZeus0200T
bin/runHADS.sh run3ServersZeus1000F
bin/runHADS.sh run3ServersZeus1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run4ServersZeus0200F
bin/runHADS.sh run4ServersZeus0200T
bin/runHADS.sh run4ServersZeus1000F
bin/runHADS.sh run4ServersZeus1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run5ServersZeus0200F
bin/runHADS.sh run5ServersZeus0200T
bin/runHADS.sh run5ServersZeus1000F
bin/runHADS.sh run5ServersZeus1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run6ServersZeus0200F
bin/runHADS.sh run6ServersZeus0200T
bin/runHADS.sh run6ServersZeus1000F
bin/runHADS.sh run6ServersZeus1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run7ServersZeus0200F
bin/runHADS.sh run7ServersZeus0200T
bin/runHADS.sh run7ServersZeus1000F
bin/runHADS.sh run7ServersZeus1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run8ServersZeus0200F
bin/runHADS.sh run8ServersZeus0200T
bin/runHADS.sh run8ServersZeus1000F
bin/runHADS.sh run8ServersZeus1000T

#############################################################################
bin/clean.sh

