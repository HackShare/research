#date
bin/clean.sh
bin/compile.sh
sleep 10

# sh -c "./run3.sh </dev/null >runs.out 2>&1 &"

bin/runHADS.sh run3ServersLocal0200T
bin/runHADS.sh run3ServersUWI0200T
bin/runHADS.sh run3ServersZeus0200T
bin/runHADS.sh run3ServersBoth0200T

bin/clean.sh

