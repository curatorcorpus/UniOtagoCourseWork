package routetests;

import domain.Customer;
import domain.Sale;
import domain.SaleItem;
import domain.Transaction;
import java.util.ArrayList;
import java.util.Collection;

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

import routes.CustomerPurchaseRB;

/**
 *
 * @author parju458
 */
public class PurchaseRouteTest extends CamelTestSupport {
    
    private String saleProduct;
    private String testTransaction;
    private Sale testSale; 
    
    private SaleItem item1 = new SaleItem("06bf537b-c7d7-11e7-ff13-0c871ec9808b", 1.0, 173.91304);
    private SaleItem item2 = new SaleItem("06bf537b-c7d7-11e7-ff13-0c871f89cbbc", 1.0, 78.26087);
    private SaleItem item3 = new SaleItem("06bf537b-c7d7-11e7-ff13-0c871f476706", 1.0, 108.69565);
    private SaleItem item4 = new SaleItem("0af7b240-abd7-11e7-eddc-390cbdd83d7f", 1.0, -86.95652);
        
    private Collection<SaleItem> items = new ArrayList<>();
    
    private Customer customer = new Customer('F', "1989-09-24");
    
    // create test data for mocks
    public PurchaseRouteTest() {
        
        items.add(item4);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        
        testSale = new Sale(items, customer, "2017-05-15T01:22:00Z");
        
        testTransaction = "{\"customer_id\":\"06bf537b-c7d7-11e7-ff13-2d958c8879b7\","
                        + "\"shop\":\"06bf537b-c7d7-11e7-ff13-2cd0cce3bc80\","
                        + "\"loyalty_points\":27}";
        
        saleProduct = "{\n" +
                        "  \"id\": \"aba1a9a5-2d87-bc61-11e7-3909d1c5cd25\",\n" +
                        "  \"source\": \"USER\",\n" +
                        "  \"source_id\": null,\n" +
                        "  \"sale_date\": \"2017-05-15T01:22:00Z\",\n" +
                        "  \"status\": \"CLOSED\",\n" +
                        "  \"user_id\": \"06bf537b-c7d7-11e7-ff13-2cd0cce3bc80\",\n" +
                        "  \"customer_id\": \"06bf537b-c7d7-11e7-ff13-2d958c8879b7\",\n" +
                        "  \"register_id\": \"06bf537b-c7d7-11e7-ff13-22f07f986719\",\n" +
                        "  \"market_id\": \"1\",\n" +
                        "  \"invoice_number\": \"4\",\n" +
                        "  \"short_code\": \"052a0w\",\n" +
                        "  \"totals\": {\n" +
                        "    \"total_price\": \"273.91304\",\n" +
                        "    \"total_loyalty\": \"0.00000\",\n" +
                        "    \"total_tax\": \"41.08696\",\n" +
                        "    \"total_payment\": \"315.00000\",\n" +
                        "    \"total_to_pay\": \"0.00000\"\n" +
                        "  },\n" +
                        "  \"note\": \"\",\n" +
                        "  \"updated_at\": \"2017-05-15T01:22:02+00:00\",\n" +
                        "  \"created_at\": \"2017-05-15 01:22:02\",\n" +
                        "  \"customer\": {\n" +
                        "    \"id\": \"06bf537b-c7d7-11e7-ff13-2d958c8879b7\",\n" +
                        "    \"customer_code\": \"Doris-C3RU\",\n" +
                        "    \"customer_group_id\": \"06bf537b-c77f-11e7-ff13-0c871e85cb6d\",\n" +
                        "    \"first_name\": \"Doris\",\n" +
                        "    \"last_name\": \"Jones\",\n" +
                        "    \"company_name\": \"\",\n" +
                        "    \"email\": \"doris@mail.com\",\n" +
                        "    \"phone\": \"\",\n" +
                        "    \"mobile\": \"\",\n" +
                        "    \"fax\": \"\",\n" +
                        "    \"balance\": \"0.000\",\n" +
                        "    \"loyalty_balance\": \"0.00000\",\n" +
                        "    \"enable_loyalty\": false,\n" +
                        "    \"points\": 0,\n" +
                        "    \"note\": \"\",\n" +
                        "    \"year_to_date\": \"3300.00000\",\n" +
                        "    \"sex\": \"F\",\n" +
                        "    \"date_of_birth\": \"1989-09-24\",\n" +
                        "    \"custom_field_1\": \"\",\n" +
                        "    \"custom_field_2\": \"\",\n" +
                        "    \"custom_field_3\": \"\",\n" +
                        "    \"custom_field_4\": \"\",\n" +
                        "    \"updated_at\": \"2017-05-14 05:59:47\",\n" +
                        "    \"created_at\": \"2017-04-30 11:09:58\",\n" +
                        "    \"deleted_at\": null,\n" +
                        "    \"contact_first_name\": \"Doris\",\n" +
                        "    \"contact_last_name\": \"Jones\"\n" +
                        "  },\n" +
                        "  \"user\": {\n" +
                        "    \"id\": \"06bf537b-c7d7-11e7-ff13-2cd0cce3bc80\",\n" +
                        "    \"name\": \"parju458\",\n" +
                        "    \"display_name\": \"Jung Woo Park\",\n" +
                        "    \"email\": \"parju458@student.otago.ac.nz\",\n" +
                        "    \"outlet_id\": null,\n" +
                        "    \"target_daily\": null,\n" +
                        "    \"target_weekly\": null,\n" +
                        "    \"target_monthly\": null,\n" +
                        "    \"created_at\": \"2017-04-29 11:41:36\",\n" +
                        "    \"updated_at\": \"2017-04-29 11:41:36\"\n" +
                        "  },\n" +
                        "  \"register_sale_products\": [\n" +
                        "    {\n" +
                        "      \"id\": \"aba1a9a5-2d87-a5ec-11e7-390ccc9d7d10\",\n" +
                        "      \"product_id\": \"0af7b240-abd7-11e7-eddc-390cbdd83d7f\",\n" +
                        "      \"quantity\": 1,\n" +
                        "      \"price\": \"-86.95652\",\n" +
                        "      \"discount\": \"0.00000\",\n" +
                        "      \"loyalty_value\": \"0.00000\",\n" +
                        "      \"price_set\": false,\n" +
                        "      \"tax\": \"-13.04348\",\n" +
                        "      \"price_total\": \"-86.95652\",\n" +
                        "      \"tax_total\": \"-13.04348\",\n" +
                        "      \"tax_id\": \"06bf537b-c77f-11e7-ff13-0c871e89e399\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"aba1a9a5-2d87-a5ec-11e7-390cd05072f3\",\n" +
                        "      \"product_id\": \"06bf537b-c7d7-11e7-ff13-0c871ec9808b\",\n" +
                        "      \"quantity\": 1,\n" +
                        "      \"price\": \"173.91304\",\n" +
                        "      \"discount\": \"0.00000\",\n" +
                        "      \"loyalty_value\": \"0.00000\",\n" +
                        "      \"price_set\": false,\n" +
                        "      \"tax\": \"26.08696\",\n" +
                        "      \"price_total\": \"173.91304\",\n" +
                        "      \"tax_total\": \"26.08696\",\n" +
                        "      \"tax_id\": \"06bf537b-c77f-11e7-ff13-0c871e89e399\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"aba1a9a5-2d87-a5ec-11e7-390cde2fa086\",\n" +
                        "      \"product_id\": \"06bf537b-c7d7-11e7-ff13-0c871f89cbbc\",\n" +
                        "      \"quantity\": 1,\n" +
                        "      \"price\": \"78.26087\",\n" +
                        "      \"discount\": \"0.00000\",\n" +
                        "      \"loyalty_value\": \"0.00000\",\n" +
                        "      \"price_set\": false,\n" +
                        "      \"tax\": \"11.73913\",\n" +
                        "      \"price_total\": \"78.26087\",\n" +
                        "      \"tax_total\": \"11.73913\",\n" +
                        "      \"tax_id\": \"06bf537b-c77f-11e7-ff13-0c871e89e399\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"aba1a9a5-2d87-a5ec-11e7-390ce0c84cc9\",\n" +
                        "      \"product_id\": \"06bf537b-c7d7-11e7-ff13-0c871f476706\",\n" +
                        "      \"quantity\": 1,\n" +
                        "      \"price\": \"108.69565\",\n" +
                        "      \"discount\": \"0.00000\",\n" +
                        "      \"loyalty_value\": \"0.00000\",\n" +
                        "      \"price_set\": false,\n" +
                        "      \"tax\": \"16.30435\",\n" +
                        "      \"price_total\": \"108.69565\",\n" +
                        "      \"tax_total\": \"16.30435\",\n" +
                        "      \"tax_id\": \"06bf537b-c77f-11e7-ff13-0c871e89e399\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"register_sale_payments\": [\n" +
                        "    {\n" +
                        "      \"id\": \"aba1a9a5-2d87-a5ec-11e7-390ce5295cfa\",\n" +
                        "      \"payment_date\": \"2017-05-15T01:22:00Z\",\n" +
                        "      \"amount\": \"315\",\n" +
                        "      \"retailer_payment_type_id\": \"06bf537b-c77f-11e7-ff13-0c871e96482b\",\n" +
                        "      \"payment_type_id\": 1,\n" +
                        "      \"retailer_payment_type\": {\n" +
                        "        \"id\": \"06bf537b-c77f-11e7-ff13-0c871e96482b\",\n" +
                        "        \"name\": \"Cash\",\n" +
                        "        \"payment_type_id\": \"1\",\n" +
                        "        \"config\": \"{\\\"rounding\\\":\\\"0.10\\\",\\\"algorithm\\\":\\\"round-mid-down\\\"}\"\n" +
                        "      },\n" +
                        "      \"payment_type\": {\n" +
                        "        \"id\": \"1\",\n" +
                        "        \"name\": \"Cash\",\n" +
                        "        \"has_native_support\": false\n" +
                        "      },\n" +
                        "      \"register_sale\": {\n" +
                        "        \"id\": \"aba1a9a5-2d87-bc61-11e7-3909d1c5cd25\",\n" +
                        "        \"source\": \"USER\",\n" +
                        "        \"source_id\": null,\n" +
                        "        \"sale_date\": \"2017-05-15T01:22:00Z\",\n" +
                        "        \"status\": \"CLOSED\",\n" +
                        "        \"user_id\": \"06bf537b-c7d7-11e7-ff13-2cd0cce3bc80\",\n" +
                        "        \"customer_id\": \"06bf537b-c7d7-11e7-ff13-2d958c8879b7\",\n" +
                        "        \"register_id\": \"06bf537b-c7d7-11e7-ff13-22f07f986719\",\n" +
                        "        \"market_id\": \"1\",\n" +
                        "        \"invoice_number\": \"4\",\n" +
                        "        \"short_code\": \"052a0w\",\n" +
                        "        \"totals\": {\n" +
                        "          \"total_price\": \"273.91304\",\n" +
                        "          \"total_loyalty\": \"0.00000\",\n" +
                        "          \"total_tax\": \"41.08696\",\n" +
                        "          \"total_payment\": \"315.00000\",\n" +
                        "          \"total_to_pay\": \"0.00000\"\n" +
                        "        },\n" +
                        "        \"note\": \"\",\n" +
                        "        \"updated_at\": \"2017-05-15T01:22:02+00:00\",\n" +
                        "        \"created_at\": \"2017-05-15 01:22:02\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"taxes\": [\n" +
                        "    {\n" +
                        "      \"id\": \"1e929694-0c87-11e7-bf13-06bf537bc77f\",\n" +
                        "      \"name\": \"GST\",\n" +
                        "      \"rate\": \"0.15000\",\n" +
                        "      \"tax\": 41.08696\n" +
                        "    }\n" +
                        "  ]\n" +
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
        RouteBuilder routeBuilder = new CustomerPurchaseRB();
        
        // create route interceptor
        routeBuilder.includeRoutes(createInterceptRoutes());
        
        return routeBuilder;
    }
    
    private RouteBuilder createInterceptRoutes() {
        
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                
                // intercept multicast endpoint
                interceptSendToEndpoint("jms:queue:vend-for-coupon")
                .skipSendToOriginalEndpoint()
                .log("Mock Vend Coupon Queue called")
                .to("mock:jms:queue:vend-for-coupon");
                
                // intercept sale aggregation service endpoint
                interceptSendToEndpoint("rmi://localhost:1099/SAS?remoteInterfaces=remoteinterface.ISaleAggregation&method=newSale")
                .skipSendToOriginalEndpoint()
                .log("Mock Sale Aggregation Service called")
                .to("mock:SAS");
                
                // intercept loyalty service endpoint
                interceptSendToEndpoint("http4://localhost:8081/customers/06bf537b-c7d7-11e7-ff13-2d958c8879b7/transactions")
                .skipSendToOriginalEndpoint()
                .log("Mock Loyalty Service Called")
                .to("mock:loyalty-service");
                
                // add dead end
                from("jms:queue:[CustPurch] 05_http-response").log("${body}");
            } 
        };
    }
    
    private void createSendSaleToVendToCouponQueueMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:jms:queue:vend-for-coupon");
		  
        vend.expectedMessageCount(1);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                
                // check that uri calls the service
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                assertStringContains(interceptedURI, "jms://queue:vend-for-coupon");     
                
                // check that previous message was multicasted correctly - examine body
                String payload = exchng.getIn().getBody(String.class);
                assertStringContains(payload, saleProduct);
            }
        });
    }
   
    private void createSASMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:SAS");
		  
        vend.expectedMessageCount(1);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                // rotates symbols around -> camel test rmi://localhost:1099/SAS?method=newSale&remoteInterfaces=remoteinterface.ISaleAggregation
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                
                assertStringContains(interceptedURI, "rmi://localhost:1099/SAS");                           // check that uri calls the service 
                assertStringContains(interceptedURI, "&remoteInterfaces=remoteinterface.ISaleAggregation"); // check that remote rmi interface is called
                assertStringContains(interceptedURI, "?method=newSale");                                    // check that the remove rmi method is called
                
                // check body is sale object to be sent to SAS
                Sale payload = exchng.getIn().getBody(Sale.class);
                
                // check sale properties
                assertEquals(payload, testSale);
            }
        });
    }
    
    private void createLoyaltyServiceMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:loyalty-service");
		  
        vend.expectedMessageCount(1);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                
                assertStringContains(interceptedURI, "http4://localhost:8081/customers/"
                                                   + "06bf537b-c7d7-11e7-ff13-2d958c8879b7"
                                                   + "/transactions");
                
                // test the customer id is set
                String propertyCustID = exchng.getProperty("customer_id", String.class);
                assertEquals(propertyCustID, "06bf537b-c7d7-11e7-ff13-2d958c8879b7");
                
                // test the correct http method is set
                String httpMethod = exchng.getIn().getHeader("CamelHttpMethod", String.class);
                assertEquals(httpMethod, "POST");
                
                // test the correct content type is set
                String contentType = exchng.getIn().getHeader("Content-Type", String.class);
                assertEquals(contentType, "application/json");
                
                // test that transaction object is added to body
                String newTransaction = exchng.getIn().getBody(String.class);
                assertEquals(newTransaction, testTransaction);
            }
        });
    }
    
    @Test
    public void testCreateCouponRouteInteraction() throws Exception {
        
        ProducerTemplate producer = this.context().createProducerTemplate();
        
        // generate endpoint route mocks
        createSendSaleToVendToCouponQueueMock();
        createSASMock();
        createLoyaltyServiceMock();
        
        // send initial payload
        producer.sendBody("jms:queue:for-testing-customer-purchase", saleProduct);
        
        assertMockEndpointsSatisfied();
    }
}