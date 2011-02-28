<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAIGKECZXA7AEIJLMQ');
    define('AWS_SECRET_KEY', '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');

    $sns = new AmazonSNS();
    $sns->set_region($sns::REGION_EU_W1);

    $topics = $sns->list_topics();
    $topics->isOK() or
        die( 'could not list topics');

    pr( $topics->body);

    function pr($var) { print '<pre>'; print_r($var); print '</pre>'; }
?>
