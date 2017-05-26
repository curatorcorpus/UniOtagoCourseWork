/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routetests;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import static org.apache.camel.test.junit4.TestSupport.assertStringContains;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import routes.CustomerViewPointsRB;

/**
 *
 * @author curator
 */
public class ViewPointsRouteTest extends CamelTestSupport {
    
    private String email; 
    private String vendCustomer;
    
    // create test data for mocks
    public ViewPointsRouteTest() {
        
        email = "boris@email.com";
        
        vendCustomer = "{\n" +
                        "        \"customers\"    :   [        {\n" +
                        "        \"id\":               \"06bf537b-c7d7-11e7-ff13-2d957f9ff0f0\",\n" +
                        "        \"name\":             \"Boris Smith\",\n" +
                        "        \"customer_code\":    \"Boris-F39Z\",\n" +
                        "        \"customer_group_id\":         \"06bf537b-c77f-11e7-ff13-0c871e85cb6d\",\n" +
                        "        \"customer_group_name\":       \"All Customers\",\n" +
                        "        \"first_name\":             \"Boris\",\n" +
                        "        \"last_name\":              \"Smith\",\n" +
                        "        \"company_name\":           \"\",\n" +
                        "        \"phone\":                  \"\",\n" +
                        "        \"mobile\":                 \"\",\n" +
                        "        \"fax\":                    \"\",\n" +
                        "        \"email\":                  \"boris@email.com\",\n" +
                        "        \"twitter\":                \"\",\n" +
                        "        \"website\" :               \"\",\n" +
                        "        \"physical_address1\":      \"\",\n" +
                        "        \"physical_address2\":      \"\",\n" +
                        "        \"physical_suburb\":        \"\",\n" +
                        "        \"physical_city\":          \"\",\n" +
                        "        \"physical_postcode\":      \"\",\n" +
                        "        \"physical_state\":         \"\",\n" +
                        "        \"physical_country_id\":    \"NZ\",\n" +
                        "        \"postal_address1\":        \"\",\n" +
                        "        \"postal_address2\":        \"\",\n" +
                        "        \"postal_suburb\":          \"\",\n" +
                        "        \"postal_city\":            \"\",\n" +
                        "        \"postal_postcode\":        \"\",\n" +
                        "        \"postal_state\":           \"\",\n" +
                        "        \"postal_country_id\" :     \"NZ\",        \"updated_at\":               \"2017-05-16 05:24:03\",\n" +
                        "        \"deleted_at\":               \"\",\n" +
                        "        \"balance\":                  \"-200.000\",\n" +
                        "        \"year_to_date\":             \"80534.60156\",\n" +
                        "        \"date_of_birth\":            \"1984-07-16\",\n" +
                        "        \"sex\":                      \"M\",\n" +
                        "        \"custom_field_1\":           \"\",\n" +
                        "        \"custom_field_2\":           \"\",\n" +
                        "        \"custom_field_3\":           \"\",\n" +
                        "        \"custom_field_4\":           \"\",\n" +
                        "        \"note\":                     \"\",\n" +
                        "        \"contact\":  {\n" +
                        "            \"company_name\":     \"\",\n" +
                        "            \"phone\":            \"\",\n" +
                        "            \"email\":            \"boris@email.com\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "        ]\n" +
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
        RouteBuilder routeBuilder = new CustomerViewPointsRB();
        
			// create route interceptor
        routeBuilder.includeRoutes(createInterceptRoutes());
        
        return routeBuilder;
    }
    
    private RouteBuilder createInterceptRoutes() {
        
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                
                // intercept vend endpoint - sends email from websocket
                interceptSendToEndpoint("https4://info323otago.vendhq.com/api/customers?email=" + email)
                .skipSendToOriginalEndpoint()
                .log("Mock Vend called")
                .to("mock:email-from-webclient");
                
                // intercept websocket endpoint - before client
                interceptSendToEndpoint("websocket://localhost:9002/loyalty_coordinator?sendToAll=true")
                .skipSendToOriginalEndpoint()
                .log("Mock Websocket called")
                .to("mock:websocket://localhost:9002/loyalty_coordinator?sendToAll=true");
            } 
        };
    }
    
    private void createSendEmailToVendMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:email-from-webclient");
		  
        vend.expectedMessageCount(1);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                
                // check that uri calls the service
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                assertStringContains(interceptedURI, "https4://info323otago.vendhq.com/api/customers?email=boris%40email.com"); // slash simple changed

                // make sure the correct http method is called
                String httpMethod = exchng.getIn().getHeader("CamelHttpMethod", String.class);
                assertStringContains(httpMethod, "GET");
                
                // make sure authorization header is added
                String authHeader = exchng.getIn().getHeader("Authorization", String.class);
                assertStringContains(authHeader, "Bearer CjOC4V9CKp10w3EkgLNtR:um8xRZhhaZpRNUXULT");
                
                // make sure the exchange property has the right email
                String payload = exchng.getProperty("email", String.class);
                assertEquals(payload, email);
                
                // replace body with new content for next route
                exchng.getIn().setBody(vendCustomer);
            }
        });
    }
   
    private void createVendResponseMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:websocket://localhost:9002/loyalty_coordinator?sendToAll=true");
		  
        vend.expectedMessageCount(1);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                
                // check that uri calls the service
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                assertStringContains(interceptedURI, "websocket://localhost:9002/loyalty_coordinator?sendToAll=true");

                // make sure it is same customer details before sending to web client
                String payload = exchng.getIn().getBody(String.class);
                assertEquals(payload, vendCustomer);
            }
        });
    }
    
    @Test
    public void testCreateCouponRouteInteraction() throws Exception {
        
        ProducerTemplate producer = this.context().createProducerTemplate();
        
        // generate endpoint route mocks
        createSendEmailToVendMock();
        createVendResponseMock();
        
        // send initial payload
        producer.sendBody("jms:queue:for-testing-viewpoints", email);
        
        assertMockEndpointsSatisfied();
    }
}
