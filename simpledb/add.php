<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAIGKECZXA7AEIJLMQ');
    define('AWS_SECRET_KEY', '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');
    define('AWS_ACCOUNT_ID', '457964863276');

    # construct the message (use zero padding to handle
    # simpledb's lexicographic ordering)
    $account = array(
        'fair' => 'yes',
        'PDFs' => sprintf( '%05d', '0'));

    $sdb = new AmazonSDB();
    # $sdb->set_region($sdb::REGION_EU_W1);

    $accounts = $sdb->create_domain( 'accounts');
    $accounts->isOK() or
            die( 'could not create domain accounts');

    $response = $sdb->put_attributes( 'accounts',
            'jurg@9apps.net', $account, true);

    pr( $response->body);

    function pr($var) { print '<pre>'; print_r($var); print '</pre>'; }
?>
