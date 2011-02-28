package net.nineapps.programmingec2.chapter4;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.DomainMetadataRequest;
import com.amazonaws.services.simpledb.model.DomainMetadataResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.nineapps.programmingec2.Constants;

/**
 * Examples for SimpleDB in Chapter 4.
 * 
 * @author flavia
 *
 */
public class SimpleDBExamples {

    public static void main(String[] args) {

        // prepare the credentials
        String accessKey = Constants.ACCESS_KEY;
        String secretKey = Constants.SECRET_KEY;

        // create the SimpleDB service
        AmazonSimpleDB sdbService = new AmazonSimpleDBClient(
            new BasicAWSCredentials(accessKey, secretKey));

        // set the endpoint for us-east-1 region
        sdbService.setEndpoint("https://sdb.amazonaws.com");
        
        new SimpleDBExamples().run(sdbService);

    }

    private void run(AmazonSimpleDB sdbService) {
        
        //-- List all SimpleDB domains --//
        List<String> domains = listDomains(sdbService);
        
        //-- List items in a domain --//
        for (String domain : domains) {
            listItems(sdbService, domain);
        }
        
        //-- Getting domain metadata --//
        for (String domain : domains) {
            showDomainMetadata(sdbService, domain);
        }
        
        
    }

    private void showDomainMetadata(AmazonSimpleDB sdbService, String domain) {
        // prepare the DomainMetadata request for this domain
        DomainMetadataRequest request = new DomainMetadataRequest(domain);
              
        DomainMetadataResult result = sdbService.domainMetadata(request);
              
        // we are interested in the total amount of items
        long totalItems = result.getItemCount();

        // show results ...
        System.out.println("Domain metadata for ["+ domain +"]: " + result);
        System.out.println("The domain " + domain + " has " +
            totalItems + " items.");
    }

    private void listItems(AmazonSimpleDB sdbService, String domain) {
        // initialize list of items
        List<Item> items = new ArrayList<Item>();

        // nextToken == null is the first page
        String nextToken = null;

        // set the select expression which retrieves all the items from this domain
        SelectRequest request = new SelectRequest("select * from " + domain);
                
        do {
            if (nextToken != null) request = request.withNextToken(nextToken);
            // make the request to the service
            SelectResult result = sdbService.select(request);

            nextToken = result.getNextToken();
            items.addAll(result.getItems());
        } while (nextToken != null);

        System.out.println("Items for domain ["+ domain +"] are: " + items);

        // show the items to user...
    }

    private List<String> listDomains(AmazonSimpleDB sdbService) {
        String nextToken = null;
        ListDomainsRequest request = new ListDomainsRequest();

        List<String> domains = new ArrayList<String>();

        // get the existing domains for this region
        do {
            if (nextToken != null) request = request.withNextToken(nextToken);
            
            ListDomainsResult result = sdbService.listDomains(request);
            nextToken = result.getNextToken();
            domains.addAll(result.getDomainNames());
            System.out.println("The existing domains are: " + domains);
            
        } while (nextToken != null);

        return domains;
    }
    
}
