#!/bin/bash
### BEGIN INIT INFO
# Provides:          ec2-instance-provisioning
# Required-Start:    $network $local_fs
# Required-Stop:     $apache2
# Should-Start:      $named
# Should-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: ec2 housekeeping
# Description:       attach/detach/mount volumes, etc.
### END INIT INFO

#
# ec2-elastic - do some ec2 housekeeping
#     (attaching/detaching volumes, mounting volumes, etc.)
#

export JAVA_HOME='/usr'
export EC2_KEY_DIR=/root/.ec2
export EC2_PRIVATE_KEY=${EC2_KEY_DIR}/pk-4P54TBID4E42U5ZMMCIZWBVYXXN6U6J3.pem
export EC2_CERT=${EC2_KEY_DIR}/cert-4P54TBID4E42U5ZMMCIZWBVYXXN6U6J3.pem
export EC2_HOME='/root/ec2-api-tools-1.3-53907'
export EC2_URL="https://eu-west-1.ec2.amazonaws.com"
PATH=$PATH:$HOME/bin:$EC2_HOME/bin 
MAX_TRIES=60

prog=$(basename $0)
logger="logger -t $prog"
curl="curl --retry 3 --silent --show-error --fail"
# this URL gives us information about the current instance
instance_data_url=http://169.254.169.254/latest
region="eu-west-1"
elastic_ip=184.72.235.156

vol="vol-c00177a9"
dev="/dev/sdf"
mnt="/var/www"

# Wait until networking is up on the EC2 instance.
perl -MIO::Socket::INET -e '
 until(new IO::Socket::INET("169.254.169.254:80")){print"Waiting for network...\n";sleep 1}
' | $logger

# start/stop functions for OS

start() {
    ctr=0
    # because the instance might change we have to get the id dynamically
    instance_id=$($curl $instance_data_url/meta-data/instance-id)

    /bin/echo "Associating Elastic IP."
    ec2-associate-address $elastic_ip -i $instance_id --region=$region

    /bin/echo "Attaching Elastic Block Store Volumes."
    ec2-attach-volume $vol -i $instance_id -d $dev --region=$region

    /bin/echo "Testing If Volumes are Attached."
    while [ ! -e "$dev" ] ; do
        /bin/sleep 1
        ctr=`expr $ctr + 1`
        # retry for maximum one minute...
        if [ $ctr -eq $MAX_TRIES ]; then
            if [ ! -e "$dev" ]; then
                /bin/echo "WARNING: Cannot attach volume $vol to $dev -- 
                    Giving up after $MAX_TRIES attempts"
            fi
        fi
    done

    if [ -e "$dev" ]; then
        if [ ! -d $mnt ]; then
            mkdir $mnt
        fi

        /bin/echo "Mounting Elastic Block Store Volumes."
        /bin/mount -t xfs -o defaults $dev $mnt
    fi
}

stop() {
    /bin/echo "Disassociating Elastic IP."
    ec2-disassociate-address $elastic_ip --region=$region

    /bin/echo "Unmounting Elastic Block Store Volumes."
    /bin/umount $mnt

    ec2-detach-volume $vol --region=$region

}


case "$1" in
    start)
        start
        ;;

    stop)
        stop
        ;;
    restart)
        stop
        sleep 5
        start
        ;;
    *)
        echo "Usage: $SELF {start|stop|restart}"
        exit 1
        ;;

esac

exit 0