<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAIGKECZXA7AEIJLMQ');
    define('AWS_SECRET_KEY', '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');
    define('AWS_ACCOUNT_ID', '457964863276');

    $queue_name = $_GET['queue'];

    $sqs = new AmazonSQS();
    $sqs->set_region($sqs::REGION_EU_W1);

    $queue = $sqs->create_queue( $queue_name);
    $queue->isOK() or die( 'could not create queue ' + $queue_name);

    $receive_response = $sqs->receive_message( $queue->body->QueueUrl(0));
    $delete_response = $sqs->delete_message( $queue->body->QueueUrl(0),
        (string)$receive_response->body->ReceiptHandle(0));
     
    $body = json_decode( $receive_response->body->Body(0));
    pr( $body);

    function pr($var) { print '<pre>'; print_r($var); print '</pre>'; }
?>
