import os, sys, re, subprocess
import json, urllib2

from boto.ec2.connection import EC2Connection
from boto.ec2.regioninfo import RegionInfo

# parameters:
#
## OLD
## - access key
## - secret key
##
# - elastic ip
# - "start"/"stop"

try:
   url = "http://169.254.169.254/latest/"
   #userdata = json.load(urllib2.urlopen(url + "user-data"))
   instance_id = urllib2.urlopen(url + "meta-data/instance-id").read()
   zone = urllib2.urlopen(url + "meta-data/placement/availability-zone").read()
   region = zone[:-1]
except Exception as e:
   print e
   exit( "We couldn't get user-data or other meta-data...")

if __name__ == '__main__':
   region_info = RegionInfo(name=region,
                           endpoint="ec2.{0}.amazonaws.com".format(region))
   #ec2 = EC2Connection(sys.argv[1], sys.argv[2], region=region_info)
   # we use IAM EC2 role to get the credentials transparently:
   ec2 = EC2Connection(region=region_info)

   address = ec2.get_all_addresses(sys.argv[1])[0]

if sys.argv[2] == "start":
   if address.instance_id == "":
     address.associate(instance_id)
   else:
     # if the elastic IP is already taken,
     # then don't do anything
     print "Elastic IP is already in use by instance " + address.instance_id

if sys.argv[2] == "stop":
  associated_instance_id = address.instance_id
  if associated_instance_id == "":
    print "The elastic IP is already free, doing nothing"
  elif associated_instance_id != instance_id:
    print "The elastic IP is associated to another instance (" +associated_instance_id+") - doing nothing"
  else:
    # the EIP is associated to this instance, disassociate it
    address.disassociate()

