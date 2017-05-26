/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routes;

import domain.Sale;
import domain.Transaction;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import org.apache.camel.Exchange;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import utils.LoyaltyPointCalculator;

/**
 *
 * @author curator
 */
public class CustomerPurchaseRB extends RouteBuilder {

    public static class TransactionCreator {
        
        public Transaction createTransaction(String id, String shop, Integer points) {
            return new Transaction(id, shop, points);
        }
    }
    
    public static String getPassword(String prompt) {
        
        JPasswordField txtPasswd = new JPasswordField();
        
        int resp = JOptionPane.showConfirmDialog(null, txtPasswd, prompt,
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resp == JOptionPane.OK_OPTION) {
            String password = new String(txtPasswd.getPassword());
            return password;
        }
        
        return null;
    }
    
    @Override
    public void configure() throws Exception {
        
        // route that obtains vend messages from email and stores it in AMQ queue.
        from("imaps://outlook.office365.com?username=parju458@student.otago.ac.nz"
            + "&password=" + getPassword("enter password") 
            + "&searchTerm.subject=Vend:SaleUpdate"
            + "&debugMode=false"     // set to true if you want to see the authentication details
            + "&folderName=Inbox")   // change to whatever folder your Vend messages end up in
            //.log("Found new Email: ${body}")
        .to("jms:queue:for-testing-customer-purchase");
            
        from("jms:queue:for-testing-customer-purchase")
            .multicast()
        .to("jms:queue:[CustPurch] 01_sale-event", "jms:queue:vend-for-coupon");
        
        // copy customer id and total price into header.
        from("jms:queue:[CustPurch] 01_sale-event")
              .setHeader("id")
                .jsonpath("$.id")
              .setHeader("shop")
                .jsonpath("$.user.id")
              .setHeader("customer_id")
               .jsonpath("$.customer_id")
              .setHeader("total_price")
                .jsonpath("$.totals.total_price")
            .multicast()
        .to("jms:queue:[CustPurch] 02_new-transaction", "jms:queue:[CustPurch] 02_new-sale");

        // create route to sales aggregation service
        from("jms:queue:[CustPurch] 02_new-sale")
             .unmarshal()
                .json(JsonLibrary.Gson, Sale.class)
        .to("rmi://localhost:1099/SAS?"
                + "remoteInterfaces=remoteinterface.ISaleAggregation&"
                + "method=newSale");

        // create route to calculate loyalty points from total price
        from("jms:queue:[CustPurch] 02_new-transaction")
             .setHeader("loyalty_points")
             .method(LoyaltyPointCalculator.class, "calculateLoyaltyPoints(" +
                                                   "${headers.total_price})")
        .to("jms:queue:[CustPurch] 03_calculated-points");
        
        // create route for converting JSON into a java beans object.
        from("jms:queue:[CustPurch] 03_calculated-points")
             .bean(TransactionCreator.class, "createTransaction(${header.customer_id},"
                                                             + "${header.shop},"            
                                                             + "${header.loyalty_points})")
        .to("jms:queue:[CustPurch] 04_send-transactions");
       
        // create route to convert transaction into JSON then send to Loyalty Service
        from("jms:queue:[CustPurch] 04_send-transactions")
            .marshal()
               .json(JsonLibrary.Gson)
               .setProperty("customer_id").header("customer_id") // copy header to exchange property
               .removeHeaders("*")                               // remove all headers to prevent then becoming http headers      
               .setHeader(Exchange.HTTP_METHOD,  constant("POST"))             // set http method type
               .setHeader(Exchange.CONTENT_TYPE, constant("application/json")) // set http content type
               .recipientList()
                .simple("http4://localhost:8081/customers/"
                      + "${exchangeProperty.customer_id}/"
                      + "transactions") // recipient list used for dynamic endpoints   
        .to("jms:queue:[CustPurch] 05_http-response");
    }
    
    // QUESTIONS TO ASK:
    // Should the transactions be serialized when transaction domain is already converted into xml.
    // can you not marshall to json from xml in camel?
    // shop?
}
