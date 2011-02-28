package com.nineapps.programmingec2.chapter7;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.Subscription;
import com.nineapps.programmingec2.Constants;

public class SNSExamples {

    /**
     * Examples for Simple Notification Service in Chapter 7.
     * 
     * @author flavia
     * 
     */
    public static void main(String[] args) {

        String accessKey = Constants.ACCESS_KEY;
        String secretKey = Constants.SECRET_KEY;
        AWSCredentials credentials = new BasicAWSCredentials(accessKey,
                secretKey);

        // get the SNS service
        AmazonSNS sns = new AmazonSNSClient(credentials);

        String nextToken = null;
        int subscriptions = 0;
        do { // call service ListSubscriptionsByTopic
            ListSubscriptionsByTopicResult result = sns
                    .listSubscriptionsByTopic(new ListSubscriptionsByTopicRequest(
                            Constants.AN_SNS_TOPIC).withNextToken(nextToken));
            nextToken = result.getNextToken();
            // show the subscriptions
            for (Subscription subscription : result.getSubscriptions()) {
                subscriptions++;
                System.out.println("Subscription: " + subscription);
            }
            // repeat until there are no more pages
        } while (nextToken != null);
        System.out.println("There are " + subscriptions
                + " subscriptions for this topic");
    }

}
