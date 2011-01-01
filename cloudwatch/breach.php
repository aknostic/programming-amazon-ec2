<?php
    require_once( '/usr/share/php/AWSSDKforPHP/sdk.class.php');

    define('AWS_KEY', 'AKIAJKI7FFIP2BHMXF7A');
    define('AWS_SECRET_KEY', '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');
    define('AWS_ACCOUNT_ID', '382146031153');

    $cw = new AmazonCloudWatch();
    $cw->set_region($cw::REGION_EU_W1);

    $measure = $_GET['measure'];
    $statistics = $_GET['statistics'];
    $unit = $_GET['unit'];
    $dimensions = explode( '=', urldecode( $_GET['dimensions']));
    $namespace = $_GET['namespace'];
    $period = $_GET['period'];
    $threshold = (int) $_GET['threshold'];

    if( !isset( $measure) ||
        !isset( $statistics) ||
        !isset( $unit) ||
        !isset( $dimensions) ||
        !isset( $namespace) ||
        !isset( $period) ||
        !isset( $threshold)) {
        exit( "Usage: http://localhost/cloudwatch/breach.php?measure=CPUUtilization&statistics=Average&unit=Percent&dimensions=ImageId%3dami-30360344&namespace=EC2&period=900&threshold=20");
    }

    $now = time();
    $before = $now - (int) $period;

    $opt = array(
            'Namespace' => 'AWS/' . $namespace,
            'Period' => (int) $period
    );

    $opt = array_merge($opt, CFComplexType::map(array(
        'Dimensions' => array(
            'member' => array(
                'Name' => $dimensions[0],
                'Value' => $dimensions[1])))));

    $response = $cw->get_metric_statistics(
        $measure, $statistics, $unit,
        date( "c", $before), date( "c", $now), $opt);

    $measurement = $response->body->member(0)->{$statistics}(0);
    $breach = $measurement >= $threshold;

    echo( $breach ? 1 : 0);
?>
