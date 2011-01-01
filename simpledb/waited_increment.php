<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAIGKECZXA7AEIJLMQ');
    define('AWS_SECRET_KEY', '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');
    define('AWS_ACCOUNT_ID', '457964863276');

    # for testing the conditional put
    $timeout = isset($_GET['timeout']) ? (int)$_GET['timeout'] : 0;

    $sdb = new AmazonSDB();
    # $sdb->set_region($sdb::REGION_EU_W1);

    $accounts = $sdb->create_domain( 'accounts');
    $accounts->isOK() or
            die( 'could not create domain accounts');

    # we have to be a bit persistent; even though it
    # is unlikely someone else might have incremented
    # during our operation
    do {
        $response = $sdb->get_attributes( 'accounts',
            'jurg@9apps.net', 'PDFs');
        $PDFs = (int)$response->body->Value(0);

        # convenience measure for testing
        sleep( $timeout);

        $account = array( 'PDFs' => sprintf( '%05d', $PDFs + 1));
        $response = $sdb->put_attributes(
            'accounts',
            'jurg@9apps.net',
            $account,
            true,
            array(
            'Expected.Name' => 'PDFs',
            'Expected.Value' => sprintf( '%05d', $PDFs)));
    } while( $response->isOK() === FALSE);

    # to read the value we have to force a consistent read
    $response = $sdb->get_attributes( 'accounts',
            'jurg@9apps.net', 'PDFs',
            array( 'ConsistentRead' => 'true'));
    pr( (int)$response->body->Value(0));

    function pr($var) { print '<pre>'; print_r($var); print '</pre>'; }
?>
