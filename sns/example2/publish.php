<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAIGKECZXA7AEIJLMQ');
    define('AWS_SECRET_KEY', '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');

    $sns = new AmazonSNS();
    $sns->set_region($sns::REGION_EU_W1);

    $topic = md5( 'jurg@9apps.net/status');
    $job = "457964863276";

    $response = $sns->create_topic( $topic);
    $response->isOK() or
        die( 'could not create topic ' + $topic);

    $response = $sns->publish(
        (string)$response->body->TopicArn(0),
        json_encode( array( "job" => $job,
                        "result" => "200",
                        "message" => "Job $job finished"))
    );

    pr( $response->body);

    function pr($var) { print '<pre>'; print_r($var); print '</pre>'; }
?>
