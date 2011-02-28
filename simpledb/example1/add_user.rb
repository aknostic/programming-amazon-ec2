require 'rubygems'
require 'right_aws'
require 'sdb/active_sdb'

RightAws::ActiveSdb.establish_connection("AKIAIGKECZXA7AEIJLMQ", 
    "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn")

# right_aws' simpledb is still alpha. it works, but it feels a bit
# experimental and does not resemble working with SQS. Anyway, you need
# to subclass Base to access a domain. The name of the class will be
# lowercased to domain name.
class Users < RightAws::ActiveSdb::Base
end

# create domain users if it doesn't exist yet,
# same as RightAws::ActiveSdb.create_domain("users")
Users.create_domain

# add Arjan
# note: id is used by right_aws sdb as the name, and names are unique
# in simpledb. but create is sort of idempotent, doens't matter if you
# create it several times, it starts to act as an update.
Users.create(
            'id' => 'arjanvw@gmail.com',
            'login' => 'mushimushi',
            'description' => 
                'total calligraphy enthousiast ink in my veins!!',
            'created_at' => '1241263900',
            'updated_at' => '1283845902',
            'admin' => '1',
            'password' => 
                '33a1c623d4f95b793c2d0fe0cadc34e14f27a664230061135',
            'salt' => 'Koma659Z3Zl8zXmyyyLQ',
            'login_count' => '22',
            'failed_login_count' => '0',
            'last_request_at' => '1271243580',
            'active' => '1',
            'agreed_terms' => '1')
