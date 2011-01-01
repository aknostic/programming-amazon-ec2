<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAIGKECZXA7AEIJLMQ');
    define('AWS_SECRET_KEY', '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');
    define('AWS_ACCOUNT_ID', '457964863276');

    # set priority
    $priority = $_GET['priority'];

    # construct the message
    $job_description = array(
        'template' => 'https://s3-eu-west-1.amazonaws.com/production/templates/223/template_1.xml',
        'assets' => 'https://s3-eu-west-1.amazonaws.com/production/assets/223',
        'result' => 'https://s3-eu-west-1.amazonaws.com/production/pdfs/223');
    $body = json_encode( $job_description);

    $sqs = new AmazonSQS();
    $sqs->set_region($sqs::REGION_EU_W1);

    if( 'high' === $priority) {
        $high_priority_jobs_queue = $sqs->create_queue( 'high-priority-jobs');
        $high_priority_jobs_queue->isOK() or
        die( 'could not create queue high-priority-jobs');

        $response = $sqs->send_message( 
            $high_priority_jobs_queue->body->QueueUrl(0),
            $body);
    } else {
        $normal_priority_jobs_queue = $sqs->create_queue( 'normal-priority-jobs');
        $normal_priority_jobs_queue->isOK() or
        die( 'could not create queue normal-priority-jobs');

        $response = $sqs->send_message( 
            $normal_priority_jobs_queue->body->QueueUrl(0),
            $body);
    }

    pr( $response->body);

    function pr($var) { print '<pre>'; print_r($var); print '</pre>'; }
?>
