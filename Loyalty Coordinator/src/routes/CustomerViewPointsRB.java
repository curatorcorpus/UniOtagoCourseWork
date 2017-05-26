/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author curator
 */
public class CustomerViewPointsRB extends RouteBuilder  {

    @Override
    public void configure() throws Exception {
        
        // create route to obtain email from websocket client
        from("websocket://localhost:9002/loyalty_coordinator")
        .to("jms:queue:for-testing-viewpoints");
        
        from("jms:queue:for-testing-viewpoints")
             .removeHeaders("*")
             .setProperty("email").body()
             .setBody(constant(null))  
             .setHeader("Authorization", constant("Bearer CjOC4V9CKp10w3EkgLNtR:um8xRZhhaZpRNUXULT"))
             .setHeader(Exchange.HTTP_METHOD, constant("GET"))
             .recipientList()
                .simple("https4://info323otago.vendhq.com/api/customers?email=${exchangeProperty.email}")
        .to("jms:queue:[CViewPts] vend-customer");
        
        // send customer details back to client
        from("jms:queue:[CViewPts] vend-customer")
             .convertBodyTo(String.class)
        .to("websocket://localhost:9002/loyalty_coordinator?sendToAll=true");
    }    
}