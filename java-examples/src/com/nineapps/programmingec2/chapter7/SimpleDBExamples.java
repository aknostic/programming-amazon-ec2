package com.nineapps.programmingec2.chapter7;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.DomainMetadataRequest;
import com.amazonaws.services.simpledb.model.DomainMetadataResult;
import com.nineapps.programmingec2.Constants;

/**
 * Examples for SimpleDB in Chapter 7.
 * 
 * @author flavia
 * 
 */
public class SimpleDBExamples {

    public static void main(String[] args) {

        String accessKey = Constants.ACCESS_KEY;
        String secretKey = Constants.SECRET_KEY;
        AWSCredentials credentials = new BasicAWSCredentials(accessKey,
                secretKey);

        // get the SimpleDB service
        AmazonSimpleDB simpleDB = new AmazonSimpleDBClient(credentials);
        // get the metadata for a domain called "my_domain" 
        DomainMetadataResult result = simpleDB.domainMetadata(
                new DomainMetadataRequest(Constants.A_SIMPLEDB_DOMAIN));
        
        // number of attributes
        // check if we already have 90% of the maximum number of attributes allowed
        if (result.getAttributeNameCount() > 900000000) {

            // ...in that case, send a warning to myself...
            System.out.println("You are reaching the maximum number of attributes allowed for a domain.");
        }

        // calculate the approximate size of the domain in bytes 
        // check if we already use 80% of the maximum allowed size of a domain 
        if ((result.getAttributeNamesSizeBytes() +
                result.getAttributeValuesSizeBytes() +
                result.getItemNamesSizeBytes()) / 1024 / 1024  > 10 * 1024 * 0.8) { 

            // ...in that case, send a warning to myself...
            System.out.println("You are reaching the maximum storage capacity of a domain");
        }
    }

}
