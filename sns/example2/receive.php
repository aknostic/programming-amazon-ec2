<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAIGKECZXA7AEIJLMQ');
    define('AWS_SECRET_KEY', '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');

    $sns = new AmazonSNS();
    $sns->set_region($sns::REGION_EU_W1);

    # we only want a POST, before we get the raw input (as json)
    if ( $_SERVER['REQUEST_METHOD'] === 'POST' ) {
        $json = trim( file_get_contents( 'php://input'));

        $notification = json_decode( $json);
        # do we have a message? or do we need to confirm?
        if( $notification->{'Type'} === 'Notification') {
            $message = json_decode( $notification->{'Message'});

            # and now we can act on the message
            # ...
        } else if( $notification->{'Type'} === 'SubscriptionConfirmation') {
            $response = $sns->confirm_subscription(
                $notification->{'TopicArn'},
                $notification->{'Token'});

            $response->isOK() or
                die( "could not confirm subscription for topic 'status'");
        }
    }
?>
