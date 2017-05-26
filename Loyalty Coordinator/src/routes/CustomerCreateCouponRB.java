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
public class CustomerCreateCouponRB extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        // obtain new coupon product to be created.
        from("websocket://localhost:9002/loyalty_coordinator/createcoupon")
        .to("jms:queue:for-testing-createcoupon");
					 
	from("jms:queue:for-testing-createcoupon")
             .removeHeaders("*")  
             .setHeader("Authorization", constant("Bearer CjOC4V9CKp10w3EkgLNtR:um8xRZhhaZpRNUXULT"))
             .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
             .setHeader(Exchange.HTTP_METHOD, constant("POST"))
             .to("https4://info323otago.vendhq.com/api/products")
        .to("jms:queue:[CCreateCoupon] vend-response");
        
        // extract and return product coupon product id
        from("jms:queue:[CCreateCoupon] vend-response")
             .setHeader("product_id").jsonpath("$.product.id")
             .setBody(constant(null))
             .setBody().header("product_id")
             .removeHeader("*")
             .convertBodyTo(String.class)
        .to("websocket://localhost:9002/loyalty_coordinator/createcoupon?sendToAll=true");
    }
}
