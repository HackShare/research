date
echo "sleeping for 54000 seconds (15 hours)"
sleep 54000
echo "sleep done"
date
#############################################################################
bin/clean.sh
bin/compile.sh
sleep 60
#############################################################################

# sh -c "./runsBoth.sh </dev/null >runsBoth.out 2>&1 &"

bin/runHADS.sh run3ServersBoth0200F
bin/runHADS.sh run3ServersBoth0200T
bin/runHADS.sh run3ServersBoth1000F
bin/runHADS.sh run3ServersBoth1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run4ServersBoth0200F
bin/runHADS.sh run4ServersBoth0200T
bin/runHADS.sh run4ServersBoth1000F
bin/runHADS.sh run4ServersBoth1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run5ServersBoth0200F
bin/runHADS.sh run5ServersBoth0200T
bin/runHADS.sh run5ServersBoth1000F
bin/runHADS.sh run5ServersBoth1000T

bin/clean.sh
bin/compile.sh
sleep 60

bin/runHADS.sh run6ServersBoth0200F
bin/runHADS.sh run6ServersBoth0200T
bin/runHADS.sh run6ServersBoth1000F
bin/runHADS.sh run6ServersBoth1000T

#############################################################################
bin/clean.sh

