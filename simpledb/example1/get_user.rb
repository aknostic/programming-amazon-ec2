require 'rubygems'
require 'right_aws'
require 'sdb/active_sdb'

RightAws::ActiveSdb.establish_connection("AKIAIGKECZXA7AEIJLMQ",
    "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")

class Users < RightAws::ActiveSdb::Base
end

# get Arjan
arjan = Users.find('arjanvw@gmail.com')
# reload to get all the attributes
arjan.reload

puts arjan.inspect
