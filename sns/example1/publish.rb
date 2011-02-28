# Sending a message allows for an option subject to be included along with
# the required message itself.

# Find the sns topic using the arn.  Note that you can call
# RightAws::SNS#create_topic if you only know the topic name
# and the current AWS api will fetch it for you.  Otherwise,
# you need to store the arn for future lookups.  It's probably
# advisable to store the arn's anyway as they serve as a reliable
# uid.

sns = RightAws::Sns.new("AKIAIGKECZXA7AEIJLMQ", 
                        "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")

arn = "arn:aws:sns:us-east-1:235698110812:12_judging"
topic = sns.topic(arn)

message = "Dear Judges, admission is closed, you can now start evaluating 
   participants."
optional_subject = "Judging begins"
topic.send_message(message, optional_subject)
