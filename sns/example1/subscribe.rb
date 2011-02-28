require "right_aws"

sns = RightAws::Sns.new("AKIAIGKECZXA7AEIJLMQ", 
                        "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")
contest_id = 12

# get the judging topic
topic = sns.create_topic("#{contest_id}_judging")

# Subscribe a few emails addresses to the topic.
# This will return "Pending Confirmation" and send a confirmation
# email to the target email address which contains a "Confirm Subscription"
# link back to Amazon Web Services.  Other possible subscription protocols
# include http, https, email, email-json and sqs

%w[alice@example.com bob@example.com].each do |email|
  topic.subscribe("email", email)
end

# Let's subscribe this address as email-json, imagining it
# is an email box feeding to a script parsing the content.  Note
# that the confirmation email this address receives will contain
# content as JSON
topic.subscribe("email-json", "carol-email-bot@example.com")

# Another kind of endpoint is http
topic.subscribe("http",
    "http://www.kulitzer.com/contests/update/#{contest_id}_judging")
