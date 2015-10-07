date
echo "sleeping for 133200 seconds (1 day 13 hours)"
sleep 133200
echo "sleep done"
date
#############################################################################
bin/clean.sh
bin/compile.sh
sleep 60
#############################################################################

# sh -c "./runsUWI.sh </dev/null >runsUWI.out 2>&1 &"

bin/runHADS.sh run3ServersUWI0200F
bin/runHADS.sh run3ServersUWI0200T
bin/runHADS.sh run3ServersUWI1000F
bin/runHADS.sh run3ServersUWI1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run4ServersUWI0200F
bin/runHADS.sh run4ServersUWI0200T
bin/runHADS.sh run4ServersUWI1000F
bin/runHADS.sh run4ServersUWI1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run5ServersUWI0200F
bin/runHADS.sh run5ServersUWI0200T
bin/runHADS.sh run5ServersUWI1000F
bin/runHADS.sh run5ServersUWI1000T

#############################################################################
bin/clean.sh

