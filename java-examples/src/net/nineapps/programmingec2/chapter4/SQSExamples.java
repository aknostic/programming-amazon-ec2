package net.nineapps.programmingec2.chapter4;

import java.util.List;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;

/**
 * Examples for Simple Queue Service in Chapter 4.
 * 
 * @author flavia
 *
 */
public class SQSExamples {

    public static void main(String[] args) {

        // XXX CHANGE TO USE YOUR OWN CREDENTIALS
        // prepare the credentials
        String accessKey = "AKIAIGKECZXA7AEIJLMQ";
        String secretKey = "w2Y3dx82vcY1YSKbJY51GmfFQn3705ftW4uSBrHn";

        // create the SQS service
        AmazonSQS sqsService = new AmazonSQSClient(
                    new BasicAWSCredentials(accessKey, secretKey));

        // XXX SET TO THE PREFERRED REGION
        // set the endpoint for us-east-1 region
        sqsService.setEndpoint("https://sqs.us-east-1.amazonaws.com");
        
        new SQSExamples().run(sqsService);

    }
    
    private void run(AmazonSQS sqsService) {
        
        //-- Getting the queues --//
        // get the current queues for this region
        List<String> queues = listQueues(sqsService);

        //-- Reading the queue attributes --//
        showAttributes(sqsService, queues);
        
        //-- Checking a specific queue attribute --//
        verifyNumberOfMessages(sqsService, queues);

    }

    private void showAttributes(AmazonSQS sqsService, List<String> queues) {
        GetQueueAttributesRequest request = new GetQueueAttributesRequest();

        // we want all the attributes of the queue
        request = request.withAttributeNames("All");

        for (String queueURL : queues) {
            // set the queue URL, which identifies the queue
            request = request.withQueueUrl(queueURL);

            // make the request to the service
            Map<String, String> attributes = sqsService.getQueueAttributes(request).getAttributes();
            
            System.out.println("attributes of queue " + queueURL + ": " + attributes);
        }
    }

    private void verifyNumberOfMessages(AmazonSQS sqsService,
            List<String> queues) {
        // get the attribute ApproximateNumberOfMessages for this queue
        GetQueueAttributesRequest request = new GetQueueAttributesRequest();
        request = request.withAttributeNames("ApproximateNumberOfMessages");
        
        int max = 25;

        for (String queueURL : queues) {
            request = request.withQueueUrl(queueURL);

            Map<String, String> attrs = sqsService.getQueueAttributes(request).getAttributes();

            // get the approximate number of messages in the queue
            int messages = Integer.parseInt(attrs.get("ApproximateNumberOfMessages"));

            // compare with max, the user's choice for maximum number of messages
            if (messages > max) {
                // if number of messages exceeds maximum, 
                // notify the user using Android notifications...
                // ...
                System.out.println("Too many messages in the queue: " + queueURL + ". Are we overloaded?");
            }
     
        }
    }

    private List<String> listQueues(AmazonSQS sqsService) {
        List<String> queues = sqsService.listQueues().getQueueUrls();
        System.out.println("Queues: " + queues);
        return queues;
    }

    
    
}
