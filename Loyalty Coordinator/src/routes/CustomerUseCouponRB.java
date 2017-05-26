/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routes;

import domain.Coupon;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 *
 * @author curator
 */
public class CustomerUseCouponRB extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        // process all product ids
        from("jms:queue:vend-for-coupon")
            .unmarshal().json(JsonLibrary.Gson, Map.class) // convert list containing maps
            .setBody().simple("${body[register_sale_products]}")
            .split().body()
            .setBody().simple("${body[product_id]}")
        .to("jms:queue:[CUseCoup] 01_product-ids");
        
        // send get all products
        from("jms:queue:[CUseCoup] 01_product-ids")
            .removeHeaders("*")
            .setProperty("product_id").body()
            .setBody(constant(null))  
            .setHeader("Authorization", constant("Bearer CjOC4V9CKp10w3EkgLNtR:um8xRZhhaZpRNUXULT"))
            .setHeader(Exchange.HTTP_METHOD, constant("GET"))
            .recipientList()
               .simple("https4://info323otago.vendhq.com/api/products/${exchangeProperty.product_id}")
        .to("jms:queue:[CUseCoup] 02_vend-products");
        
        // content router - sort out coupon products
        from("jms:queue:[CUseCoup] 02_vend-products")
            .unmarshal().json(JsonLibrary.Gson, Map.class)
            .setBody().simple("${body[products]}")    
            .choice()
                .when().simple("${body[0][type]} == 'Coupon'")
                    .setBody().simple("${body[0]}")
            .multicast()
        .to("jms:queue:[CUseCoup] 03_coupon-4-vend", "jms:queue:[CUseCoup] 03_coupon-4-loyalty-service");

        // to update coupon from loyalty service
        from("jms:queue:[CUseCoup] 03_coupon-4-loyalty-service")
            .removeHeaders("*")  
            .setProperty("customer_id").simple("${body[handle]}")
            .setProperty("coupon_id").simple("${body[source_id]}")
            .setBody(constant(null))
            .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
            .setHeader(Exchange.ACCEPT_CONTENT_TYPE).constant("application/json")
            .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .recipientList()
                    .simple("http4://localhost:8081/customers/${exchangeProperty.customer_id}/" +
                            "coupons/${exchangeProperty.coupon_id}") // -1000 for testing
        .to("direct:[CUseCoup] 04_loyalty-service-coupon-2-update");        
       
        // set coupon used property to true and update
        from("direct:[CUseCoup] 04_loyalty-service-coupon-2-update")
            .unmarshal().json(JsonLibrary.Gson, Coupon.class)
            .process(new Processor() {
                @Override
                public void process(Exchange exchng) throws Exception {
                    Coupon coup = exchng.getIn().getBody(Coupon.class);
                    
                    coup.setUsed(true);
                    
                    exchng.getIn().setBody(coup);
                }})
            .marshal().json(JsonLibrary.Gson)
            .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
            .setHeader(Exchange.ACCEPT_CONTENT_TYPE).constant("application/json")                
            .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .recipientList()
                    .simple("http4://localhost:8081/customers/${exchangeProperty.customer_id}/" +
                            "coupons/${exchangeProperty.coupon_id}") // -1000 for testing
        .to("jms:queue:[CUseCoup] 05-loyalty-service-coupon-update-response");   
       
        // delete the coupon in vend
        from("jms:queue:[CUseCoup] 03_coupon-4-vend")
             .removeHeaders("*")  
             .setProperty("product_id").simple("${body[id]}")
             .setBody(constant(null))  
             .setHeader("Authorization", constant("Bearer CjOC4V9CKp10w3EkgLNtR:um8xRZhhaZpRNUXULT"))
             .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
             .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .recipientList()
                    .simple("https4://info323otago.vendhq.com/api/products/${exchangeProperty.product_id}")
        .to("jms:queue:[CUseCoup] 04_vend-coupon-delete-response");
    }
}
