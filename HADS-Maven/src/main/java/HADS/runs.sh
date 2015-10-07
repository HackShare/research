#date
#echo "sleeping for 54000 seconds"
#sleep 54000
#echo "sleep done"
#date
bin/clean.sh
bin/compile.sh

# sh -c "./runs.sh </dev/null >runs.out 2>&1 &"

#200 ms w sub i = 1/n
#200 ms w sub i not 1/n but based on work done
#1000 ms w sub i not 1/n but based on work done
#1000 ms w sub i = 1/n

#bin/runHADS.sh run3ServersLocal
#bin/runHADS.sh run4ServersLocal

#bin/runHADS.sh run3ServersUWI
#bin/runHADS.sh run3ServersZeus
#bin/runHADS.sh run3ServersBoth

#bin/clean.sh
#bin/compile.sh

#bin/runHADS.sh run4ServersUWI
#bin/runHADS.sh run4ServersZeus
#bin/runHADS.sh run4ServersBoth

#bin/clean.sh
#bin/compile.sh

#bin/runHADS.sh run5ServersUWI
#bin/runHADS.sh run5ServersZeus
#bin/runHADS.sh run5ServersBoth

#bin/clean.sh
#bin/compile.sh

#bin/runHADS.sh run6ServersBoth

#bin/runHADS.sh run5ServersLocal
#bin/runHADS.sh run6ServersLocal
#bin/runHADS.sh run7ServersLocal

#bin/runHADS.sh run6ServersZeus
#bin/runHADS.sh run7ServersZeus
#bin/runHADS.sh run8ServersZeus

bin/runHADS.sh run4ServersLocal
bin/runHADS.sh run5ServersZeus
bin/runHADS.sh run6ServersZeus

bin/clean.sh

