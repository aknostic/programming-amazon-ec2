package com.nineapps.programmingec2.chapter7;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.nineapps.programmingec2.Constants;

/**
 * Examples for Simple Queue Service in Chapter 7.
 * 
 * @author flavia
 * 
 */
public class SQSExamples {

    public static void main(String[] args) throws ParseException {

        String accessKey = Constants.ACCESS_KEY;
        String secretKey = Constants.SECRET_KEY;
        AWSCredentials credentials = new BasicAWSCredentials(accessKey,
                secretKey);

        // get the SQS service
        AmazonSQS sqs = new AmazonSQSClient(credentials);
        List<String> attributes = new ArrayList<String>();
        attributes.add("All");

        SQSLogger sqsLogger = new SQSLogger(credentials);

        SQSExamples examples = new SQSExamples();
        
        // first we send some messages to have something to measure...
        examples.sendSomeMessages(sqs);
        
        // we receive a message from SQS, process it
        // log it and delete it if successfully processed
        examples.receiveAndLogMessage(sqs, attributes, sqsLogger);

        // show the attributes of the queue
        examples.showAttributes(sqs);
        
        // show latency of the queue
        examples.showLatency(sqsLogger);
        
        // show the throughput of the queue
        examples.showThroughput(sqsLogger);
    }

    private void sendSomeMessages(AmazonSQS sqs) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sqs.sendMessage(new SendMessageRequest(Constants.QUEUE_URL, 
                "a random message sent at " + format.format(new Date())));
    }

    private void showThroughput(SQSLogger sqsLogger) {
        System.out.println("Throughput in the last hour is " + sqsLogger.getThroughput(60 * 60));
        System.out.println("Throughput in the last day is " + sqsLogger.getThroughput(24 * 60 * 60));
        System.out.println("Throughput in the last week is " + sqsLogger.getThroughput(7 * 24 * 60 * 60));
        System.out.println("Throughput in the last month is " + sqsLogger.getThroughput(30 * 24 * 60 * 60));        
    }

    private void showLatency(SQSLogger sqsLogger) throws ParseException {
        System.out.println("Latency (in minutes) for the last hour is " 
                + sqsLogger.getLatency(60 * 60) / 1000.0f / 60);
        System.out.println("Latency (in minutes) for the last day is " 
                + sqsLogger.getLatency(24 * 60 * 60) / 1000.0f / 60);
        System.out.println("Latency (in minutes) for the last week is " 
                + sqsLogger.getLatency(7 * 24 * 60 * 60) / 1000.0f / 60); 
        System.out.println("Latency (in minutes) for the last month is " 
                + sqsLogger.getLatency(30 * 24 * 60 * 60) / 1000.0f / 60);
    }

    private void showAttributes(AmazonSQS sqs) {
        // get all the attributes of the queue 
        List<String> attributeNames = new ArrayList<String>();
        attributeNames.add("All");

        // list the attributes of the queue we are interested in
        GetQueueAttributesRequest request = new GetQueueAttributesRequest(
                Constants.QUEUE_URL);
        request.setAttributeNames(attributeNames);
        Map<String, String> attributes = sqs.getQueueAttributes(request)
                .getAttributes();
        int messages = Integer.parseInt(attributes
                .get("ApproximateNumberOfMessages"));
        int messagesNotVisible = Integer.parseInt(attributes
                .get("ApproximateNumberOfMessagesNotVisible"));
        System.out.println("Messages in the queue: " + messages);
        System.out.println("Messages not visible: " + messagesNotVisible);
        System.out.println("Total messages in the queue: " + messages
                + messagesNotVisible);
    }

    private void receiveAndLogMessage(AmazonSQS sqs, List<String> attributes,
            SQSLogger sqsLogger) {
        // receive messages from SQS
        ReceiveMessageRequest request = new ReceiveMessageRequest(
                Constants.QUEUE_URL);
        request.setAttributeNames(attributes);

        ReceiveMessageResult result = sqs.receiveMessage(request);
        List<Message> messages = result.getMessages();

        for (Message message : messages) {
            System.out.println("receiving message " + message.getMessageId());
            Date pickedUpTime = new Date();
            // process the message, and delete it from the queue...
            boolean succeeded = process(message);
            sqsLogger.logMessage(message, pickedUpTime, succeeded);
        }
    }

    private boolean process(Message message) {
        return true;
    }

}
