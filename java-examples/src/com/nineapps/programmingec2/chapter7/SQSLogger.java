package com.nineapps.programmingec2.chapter7;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.amazonaws.services.sqs.model.Message;

public class SQSLogger {

    private AmazonSimpleDB simpleDB;
    private SimpleDateFormat format;

    public SQSLogger(AWSCredentials credentials) {
        // get the SimpleDB service
        simpleDB = new AmazonSimpleDBClient(credentials);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Log an SQS message into the SimpleDB domain "sqs_log". pickedUpTime will
     * be used to calculate latency If the message was been successfully
     * processed we save also the time at which it was processed (deleted from
     * the queue).
     */
    public void logMessage(Message message, Date pickedUpTime, boolean succeeded) {
        String timestamp = message.getAttributes().get("SentTimestamp");
        String sentTimestamp = format
                .format(new Date(Long.parseLong(timestamp)));
        List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();
        attributes.add(new ReplaceableAttribute("SentTimestamp", sentTimestamp,
                true));
        // All attributes are set to replace=true
        // except for PickedUpTimestamp
        // since the message could be picked up several times
        // until it is successfully processed
        // For latency we need the earliest PickedTimestamp
        attributes.add(new ReplaceableAttribute("PickedUpTimestamp", format
                .format(pickedUpTime), false));
        attributes.add(new ReplaceableAttribute("MessageBody", message
                .getBody(), true));
        if (succeeded) {
            attributes.add(new ReplaceableAttribute("ProcessedTimestamp",
                    format.format(new Date()), true));
        }
        // create an item in SimpleDB for this message
        PutAttributesRequest request = new PutAttributesRequest("sqs_log", // simpledb
                // domain
                // name
                message.getMessageId(), // item name
                attributes);

        simpleDB.putAttributes(request);
    }

    /**
     * Get the average latency of messages served from the given start datetime
     * to the given end datetime. Return value is a long expressed in
     * milliseconds
     **/
    public long getLatency(Date start, Date end) throws ParseException {
        long count = 0;
        long totalLatency = 0;
        String nextToken = null;
        // retrieve all the items which are in the
        // date range we want
        SelectRequest request = new SelectRequest(
                "select SentTimestamp, PickedUpTimestamp, MessageBody "
                        + "from sqs_log " + "where SentTimestamp > '"
                        + format.format(start) + "' "
                        + "and PickedUpTimestamp < '" + format.format(end)
                        + "'");
        do {
            request.setNextToken(nextToken);
            SelectResult result = simpleDB.select(request);
            nextToken = result.getNextToken();

            for (Item item : result.getItems()) {

                String sentTimestamp = null;
                String pickedUpTimestamp = null;

                for (Attribute attribute : item.getAttributes()) {

                    if ("SentTimestamp".equals(attribute.getName())) {
                        sentTimestamp = attribute.getValue();

                    } else if ("PickedUpTimestamp".equals(attribute.getName())) {

                        // we need the earliest PickedUpTimestamp
                        if (pickedUpTimestamp == null
                                || pickedUpTimestamp.compareTo(attribute
                                        .getValue()) > 0) {
                            pickedUpTimestamp = attribute.getValue();
                        }
                    }
                }
                totalLatency += format.parse(pickedUpTimestamp).getTime()
                        - format.parse(sentTimestamp).getTime();
                count++;
            }
        } while (nextToken != null);
        // return the average
        return (count != 0 ? totalLatency / count : 0);
    }

    /**
     * Helper method which returns the latency of the past given period of time,
     * in miliseconds.
     **/
    public long getLatency(int seconds) throws ParseException {
        Date now = new Date();
        Calendar before = Calendar.getInstance();
        before.setTime(now);
        before.add(Calendar.SECOND, -seconds);
        return getLatency(before.getTime(), now);
    }

    /**
     * Returns the number of messages served from the given start timestamp to
     * the end timestamp.
     **/
    public long getThroughput(Date start, Date end) {

        SelectRequest request = new SelectRequest("select count(*) "
                + "from sqs_log " + "where SentTimestamp > '"
                + format.format(start) + "' " + "and ProcessedTimestamp < '"
                + format.format(end) + "'");

        SelectResult result = simpleDB.select(request);
        for (Attribute attribute : result.getItems().get(0).getAttributes()) {
            if ("Count".equals(attribute.getName())) {
                return Long.parseLong(attribute.getValue());
            }
        }
        return 0;
    }

    /**
     * Helper method which returns the number of messages served in the past
     * given period of time.
     **/
    public long getThroughput(int seconds) {
        Date now = new Date();
        Calendar before = Calendar.getInstance();
        before.setTime(now);
        before.add(Calendar.SECOND, -seconds);
        return getThroughput(before.getTime(), now);
    }
}
