require 'rubygems'
require 'right_aws'

# get SQS service with AWS credentials
sqs = RightAws::SqsGen2.new("AKIAIGKECZXA7AEIJLMQ", 
                             "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")

# create the queue, if it doesn't exist, with a VisibilityTimeout of 120 (seconds)
images_queue = sqs.queue("images", true, 120)

# the only thing we need to pass is the URL to the image in S3
images_queue.send_message( 
  "https://s3.amazonaws.com/media.kulitzer.com.production/212/24/replication.jpg")
