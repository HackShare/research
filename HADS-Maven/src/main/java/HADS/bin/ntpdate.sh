for i in antonio.rowan.edu boerne.rowan.edu poteet.rowan.edu schertz.rowan.edu austin.rowan.edu dallas.rowan.edu houston.rowan.edu; do echo $i; ssh knoppix@$i "sudo ntpdate 150.250.1.1"; echo $i; done

