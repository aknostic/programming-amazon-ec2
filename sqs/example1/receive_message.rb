require 'rubygems'
require 'right_aws'

sqs = RightAws::SqsGen2.new("AKIAIGKECZXA7AEIJLMQ",
                             "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")

# create the queue, if it doesn't exist, with a VisibilityTimeout of 120 (seconds)
images_queue = sqs.queue("images", true, 120)

# now get the messages
message = images_queue.receive

# process the message, if any
# the process method is application-specific
if (process(message.body)) then
    message.delete
end
