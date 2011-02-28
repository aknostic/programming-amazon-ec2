require "right_aws"

# Now, imagine you subscribe an internal resource to the topic and instruct
# it to use the http protocol and post to a url you provide.  It will receive
# a post including the following bit of JSON data:
=begin
{
 "Type" : "SubscriptionConfirmation",
 "MessageId" : "b00e1384-6a4f-4bc5-abd5-9b7f82e3cff4",
 "Token" : "51b2ff3edb4487553c7dd2f29566c2aecada20b9…",
 "TopicArn" : "arn:aws:sns:us-east-1:235698110812:12_judging",
 "Message" : "You have chosen to subscribe to the topic
    arn:aws:sns:us-east-1:235698110812:12_judging.\n
    To confirm the subscription, visit the SubscribeURL included in this
    message.",
 "SubscribeURL" : 
    "https://sns.us-east-1.amazonaws.com/?Action=ConfirmSubscription&
    TopicArn=arn:aws:sns:us-east-1:235698110812:12_judging&
        Token=51b2ff3edb4487553c7dd2f29566c2aecada20b9…",
 "Timestamp" : "2011-01-07T11:41:02.417Z",
 "SignatureVersion" : "1",
 "Signature" : "UHWoZfMkpH/FrhICs6An0cTtjjcj5nBEweVbWgrARD5B…"
}
=end

# You can confirm the subscription with a call to confirm_subscription.
# Note that in order to receive messages published to a topic, the subscriber
# must be confirmed.

sns = RightAws::Sns.new("AKIAIGKECZXA7AEIJLMQ", 
                        "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")

arn = "arn:aws:sns:us-east-1:724187402011:12_judging"
topic = sns.topic(arn)

token = "a_very_long_token_string"
topic.confirm_subscription(token)
