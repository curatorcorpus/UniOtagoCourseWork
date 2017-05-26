package routetests;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import routes.CustomerCreateCouponRB;

/**
 *
 * @author parju458
 */
public class CreateCouponRouteTest extends CamelTestSupport {

    private String couponProduct; 
    private String vendCouponProduct;
    
    // create test data for mocks
    public CreateCouponRouteTest() {
        
        couponProduct = "{\n" +
                            "\"source_id\": 1000,\n" +
                            "\"source_variant_id\": \"\"," +
                            "\"handle\": \"test\"," +
                            "\"type\": \"Coupon\"," + 
                            "\"tags\": \"loyalty\"," +
                            "\"name\": \"Coupon For Boris Test\"," + 
                            "\"description\": \"Discounts Price with Loyalty Points\"," + 
                            "\"sku\": \"\"," +
                            "\"variant_option_one_name\": \"\"," +
                            "\"variant_option_one_value\": \"\"," +
                            "\"variant_option_two_name\": \"\"," +
                            "\"variant_option_two_value\": \"\"," +
                            "\"variant_option_three_name\": \"\"," +
                            "\"variant_option_three_value\": \"\"," +
                            "\"supply_price\": \"\"," +
                            "\"retail_price\": \"-1000\"," +
                            "\"tax\": \"GST\"," +
                            "\"brand_name\": \"\"," + 
                            "\"supplier_name\": \"\"," +
                            "\"supplier_code\": \"\"," +
                            "\"inventory\": [" +
                            "{\n" + 
                                "\"outlet_name\": \"\"," +
                                "\"count\": \"0\"," + 
                                "\"reorder_point\": \"0\"," +
                                "\"restock_level\": \"0\"\n" +
                            "}]\n" + 
                        "}";
        
        // note to self coupon and this vend product are different
        vendCouponProduct = "{\n" +
                            "    \"product\"    :\n" +
                            "        {\n" +
                            "\"id\":               \"0af7b240-abd7-11e7-eddc-3a09fdd77072\",\n" +
                            "\"source_id\":        \"1000\",\n" +
                            "\"variant_source_id\": \"\",\n" +
                            "\"handle\":           \"06bf537b-c7d7-11e7-ff13-2d957f9ff0f0\",\n" +
                            "\"type\":           \"\",\n" +
                            "\"has_variants\":   false,\n" +
                            "\"variant_parent_id\":  \"0af7b240-abd7-11e7-eddc-3908944e32af\",\n" +
                            "\"variant_option_one_name\": \"\",\n" +
                            "\"variant_option_one_value\": \"\",\n" +
                            "\"variant_option_two_name\": \"\",\n" +
                            "\"variant_option_two_value\": \"\",\n" +
                            "\"variant_option_three_name\": \"\",\n" +
                            "\"variant_option_three_value\": \"\",\n" +
                            "\"active\":           true,\n" +
                            "\"name\":             \"Coupon For Boris Smith\",\n" +
                            "\"base_name\":        \"Coupon For Boris Smith\",\n" +
                            "\"description\":      \"Discounts Price with Loyalty Points\",\n" +
                            "\"image\":            \"\",\n" +
                            "\"image_large\":      \"\",\n" +
                            "\"images\":           null,\n" +
                            "\"sku\":              \"10272\",\n" +
                            "\"tags\":             \"\",\n" +
                            "\"brand_id\":         \"\",\n" +
                            "\"brand_name\":       \"\",\n" +
                            "\"supplier_name\":    \"\",\n" +
                            "\"supplier_code\":    \"\",\n" +
                            "\"supply_price\":     \"0.00\",\n" +
                            "\"account_code_purchase\":     \"\",\n" +
                            "\"account_code_sales\":        \"\",\n" +
                            "\"track_inventory\":           true,\n" +
                            "\"button_order\":           \"\",\n" +
                            "    \"inventory\": [],\n" +
                            "    \"price_book_entries\":    [],\n" +
                            "\"price\":    -86.95652,\n" +
                            "\"tax\":      0,\n" +
                            "\"tax_id\":   \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                            "\"tax_rate\": 0,\n" +
                            "\"tax_name\": \"\",\n" +
                            "\"taxes\":    null,\n" +
                            "\"display_retail_price_tax_inclusive\"          :       1,\n" +
                            "\"updated_at\":       \"2017-05-16 19:33:45\",\n" +
                            "\"deleted_at\":       \"\"\n" +
                            "}\n" +
                            "}";
    }
    
    @Override 
    protected CamelContext createCamelContext() throws Exception {
        // Create test camel context
        CamelContext camel = super.createCamelContext();

        // use direct instead of jms
        camel.addComponent("jms", camel.getComponent("direct"));

        return camel;
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        // create actual route builder
        RouteBuilder routeBuilder = new CustomerCreateCouponRB();
        
	// create route interceptor
        routeBuilder.includeRoutes(createInterceptRoutes());
        
        return routeBuilder;
    }
    
    private RouteBuilder createInterceptRoutes() {
        
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                
                // intercept vend endpoint - sends coupon from websocket
                interceptSendToEndpoint("https4://info323otago.vendhq.com/api/products")
                .skipSendToOriginalEndpoint()
                .log("Mock Vend called")
                .to("mock:coupon-from-webclient");
                
                // intercept websocket endpoint - before client
                interceptSendToEndpoint("websocket://localhost:9002/loyalty_coordinator/createcoupon?sendToAll=true")
                .skipSendToOriginalEndpoint()
                .log("Mock Websocket called")
                .to("mock:websocket://localhost:9002/loyalty_coordinator/createcoupon");
            } 
        };
    }
    
    private void createSendCouponToVendMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:coupon-from-webclient");
		  
        vend.expectedMessageCount(1);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                
                // check that uri calls the service
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                assertStringContains(interceptedURI, "https4://info323otago.vendhq.com/api/products");

                // make sure the correct http method is called
                String httpMethod = exchng.getIn().getHeader("CamelHttpMethod", String.class);
                assertStringContains(httpMethod, "POST");
                
                // make sure the correct content type is assigned
                String contentType = exchng.getIn().getHeader("Content-Type", String.class);
                assertStringContains(contentType, "application/json");
                
                // make it is the same coupon json body
                String payload = exchng.getIn().getBody(String.class);
                assertEquals(couponProduct, payload);
                
                // replace body with new content for next endpoint
                exchng.getIn().setBody(vendCouponProduct);
            }
        });
    }
   
    private void createVendResponseMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:websocket://localhost:9002/loyalty_coordinator/createcoupon");
		  
        vend.expectedMessageCount(1);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                
                // check that uri calls the service
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                assertStringContains(interceptedURI, "websocket://localhost:9002/loyalty_coordinator/createcoupon?sendToAll=true");

                // make it is the same coupon json body
                String payload = exchng.getIn().getBody(String.class);
                assertEquals(payload, "0af7b240-abd7-11e7-eddc-3a09fdd77072");
            }
        });
    }
    
    @Test
    public void testCreateCouponRouteInteraction() throws Exception {
        
        ProducerTemplate producer = this.context().createProducerTemplate();
        
        // generate endpoint route mocks
        createSendCouponToVendMock();
        createVendResponseMock();
        
        // send initial payload
        producer.sendBody("jms:queue:for-testing-createcoupon", couponProduct);
        
        assertMockEndpointsSatisfied();
    }
}
