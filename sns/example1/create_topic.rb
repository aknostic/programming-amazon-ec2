require "right_aws"

sns = RightAws::Sns.new("AKIAIGKECZXA7AEIJLMQ", 
                        "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")
contest_id = 12

# create the topics

registration = sns.create_topic("#{contest_id}_registration")
# set a human readable name for the topic
registration.display_name = "Registration process begins"

admission = sns.create_topic("#{contest_id}_admission")
admission.display_name = "Admission process begins"

judging = sns.create_topic("#{contest_id}_judging")
judging.display_name = "Judging process begins"
