package net.nineapps.programmingec2.chapter4;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;
import com.nineapps.programmingec2.Constants;

/**
 * Examples for Simple Notification Service in Chapter 4.
 * 
 * @author flavia
 *
 */
public class SNSExamples {

    public static void main(String[] args) {

        // prepare the credentials
        String accessKey = Constants.ACCESS_KEY;
        String secretKey = Constants.SECRET_KEY;

        // create the SNS service
        AmazonSNS snsService = new AmazonSNSClient(
            new BasicAWSCredentials(accessKey, secretKey));

        // XXX SET TO THE PREFERRED REGION
        // set the endpoint for us-east-1 region
        snsService.setEndpoint("https://sns.us-east-1.amazonaws.com");

        new SNSExamples().run(snsService);
    
    }

    public void run(AmazonSNS snsService) {
        
        //-- List topics --//
        List<Topic> topics = listTopics(snsService);
        
        //-- Subscribe to topic via email --//
        // fake email to subscribe (see inbox in www.mailinator.com)
        String address = "decaf-notifications@mailinator.com";
        // subscribe to all the topics listed
        for (Topic topic : topics) {
            subscribeToTopic(snsService, address, topic);
        }
        
        //-- Send a notification on a topic --//
        // send a notification for all topics
        for (Topic topic : topics) {
            sendNotification(snsService, topic);
        }
    }

    private void sendNotification(AmazonSNS snsService, Topic topic) {
        String topicARN = topic.getTopicArn(); 

        snsService.publish(new PublishRequest(topicARN,
            "A server is in trouble!", "server alarm"));
    }

    private void subscribeToTopic(AmazonSNS snsService, String address,
            Topic topic) {
        String topicARN = topic.getTopicArn(); 

        // subscribe the user to the topic with protocol = "email"
        snsService.subscribe(new SubscribeRequest(topicARN, "email", address));
    }

    private List<Topic> listTopics(AmazonSNS snsService) {
        List<Topic> topics = new ArrayList<Topic>();
        String nextToken = null;

        do {

            // create the request, with nextToken if not empty
            ListTopicsRequest request = new ListTopicsRequest();
            if (nextToken != null) request = request.withNextToken(nextToken);

                // call the web service
                ListTopicsResult result = snsService.listTopics(request);

                nextToken = result.getNextToken();

                // get that list of topics
                topics.addAll(result.getTopics());

        // go on if there are more elements    
        } while (nextToken != null);
        
        System.out.println("Topics: " + topics);

        // show the list of topics...
        
        return topics;
    }
    
}
