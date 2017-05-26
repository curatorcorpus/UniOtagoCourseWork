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
import org.junit.Test;
import routes.CustomerUseCouponRB;

/**
 *
 * @author curator
 */
public class UseCouponRouteTest extends CamelTestSupport {

    private String testSaleProduct;
    
    private String productID1, productID2, productID3, productID4;
    private String vendTestProduct1, vendTestProduct2, vendTestProduct3, vendTestProduct4;
    
    private String couponProductCustomerID;
    private String couponIDLoyaltyService;
    
    private String testCoupon;
    
    // create test data for mocks
    public UseCouponRouteTest() {

        couponIDLoyaltyService = "-1000";
        couponProductCustomerID = "06bf537b-c7d7-11e7-ff13-2d958c8879b7";

        testCoupon = "{\"id\":-1000,\"points\":2000,\"used\":false}";
        
        vendTestProduct1 = "{\n" +
                    "        \"products\"    :   [        {\n" +
                    "\"id\":               \"0af7b240-abd7-11e7-eddc-390cbdd83d7f\",\n" +
                    "\"source_id\":        \"-1000\",\n" +
                    "\"variant_source_id\": \"\",\n" +
                    "\"handle\":           \"06bf537b-c7d7-11e7-ff13-2d958c8879b7\",\n" +
                    "\"type\":           \"Coupon\",\n" +
                    "\"has_variants\":   false,\n" +
                    "\"variant_parent_id\":  \"\",\n" +
                    "\"variant_option_one_name\": \"\",\n" +
                    "\"variant_option_one_value\": \"\",\n" +
                    "\"variant_option_two_name\": \"\",\n" +
                    "\"variant_option_two_value\": \"\",\n" +
                    "\"variant_option_three_name\": \"\",\n" +
                    "\"variant_option_three_value\": \"\",\n" +
                    "\"active\":           true,\n" +
                    "\"name\":             \"Sunglasses\",\n" +
                    "\"base_name\":        \"Sunglasses\",\n" +
                    "\"description\":      \"Retro square sunglasses with tinted or polarizing lenses.\",\n" +
                    "\"image\":            \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-thumb.png\",\n" +
                    "\"image_large\":      \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-original.png\",\n" +
                    "\"images\":           [],\n" +
                    "\"sku\":              \"10012\",\n" +
                    "\"tags\":             \"Retro square, Summer\",\n" +
                    "\"brand_id\":         \"06bf537b-c7d7-11e7-ff13-0c871ed74273\",\n" +
                    "\"brand_name\":       \"In The Shade\",\n" +
                    "\"supplier_name\":    \"Ramons\",\n" +
                    "\"supplier_code\":    \"\",\n" +
                    "\"supply_price\":     \"50.00\",\n" +
                    "\"account_code_purchase\":     \"\",\n" +
                    "\"account_code_sales\":        \"\",\n" +
                    "\"track_inventory\":           true,\n" +
                    "\"button_order\":           \"\",\n" +
                    "    \"inventory\": [{\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3eb7216ea\",\n" +
                    "        \"outlet_name\"    :       \"Josh C.&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc0375f365d\",\n" +
                    "        \"outlet_name\"    :       \"Caleb&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\n" +
                    "        \"outlet_name\"    :       \"Main Outlet\",\n" +
                    "        \"count\"          :       \"2.00000\",\n" +
                    "        \"reorder_point\"          :       \"1\",\n" +
                    "        \"restock_level\"          :       \"3\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2ca699476\",\n" +
                    "        \"outlet_name\"    :       \"Hamish&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-5.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc1b5487984\",\n" +
                    "        \"outlet_name\"    :       \"Charlie&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-8.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc46908e7fa\",\n" +
                    "        \"outlet_name\"    :       \"Nic&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc30beac6d6\",\n" +
                    "        \"outlet_name\"    :       \"Isaac&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-2.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc50b2da27a\",\n" +
                    "        \"outlet_name\"    :       \"Shaye&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-5.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3719f437c\",\n" +
                    "        \"outlet_name\"    :       \"James&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-23.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc44ae9f216\",\n" +
                    "        \"outlet_name\"    :       \"Kurt&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-2.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2f640ca94\",\n" +
                    "        \"outlet_name\"    :       \"Huiyu&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc348412b06\",\n" +
                    "        \"outlet_name\"    :       \"Jacques&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-4.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3abeba818\",\n" +
                    "        \"outlet_name\"    :       \"Jonathan&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4a384466a\",\n" +
                    "        \"outlet_name\"    :       \"Oscar&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1199546.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\n" +
                    "        \"outlet_name\"    :       \"Noel&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-14.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cbfccecfcbf\",\n" +
                    "        \"outlet_name\"    :       \"Annabelle&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-2.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2e1ccdba1\",\n" +
                    "        \"outlet_name\"    :       \"Harry&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-4.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc27fbd567c\",\n" +
                    "        \"outlet_name\"    :       \"Elijah&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-7.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc5722df835\",\n" +
                    "        \"outlet_name\"    :       \"William S.&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-25.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc26595c692\",\n" +
                    "        \"outlet_name\"    :       \"Daniel P.&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-163.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2b69f637e\",\n" +
                    "        \"outlet_name\"    :       \"Guy&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc38db61190\",\n" +
                    "        \"outlet_name\"    :       \"Joe&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-5.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc1fd889603\",\n" +
                    "        \"outlet_name\"    :       \"Daniel T.&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-2.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc54f679dde\",\n" +
                    "        \"outlet_name\"    :       \"Will&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-2.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc59bcffab7\",\n" +
                    "        \"outlet_name\"    :       \"William T.&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4883fdfb4\",\n" +
                    "        \"outlet_name\"    :       \"Oliver&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-13.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4f56572e8\",\n" +
                    "        \"outlet_name\"    :       \"Rory&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-25.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\n" +
                    "        \"outlet_name\"    :       \"Greg&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-2.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4150632e9\",\n" +
                    "        \"outlet_name\"    :       \"Josh W.&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-10.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3cda4e809\",\n" +
                    "        \"outlet_name\"    :       \"Jordan&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-7.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cbff716ca41\",\n" +
                    "        \"outlet_name\"    :       \"Ayesha&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-2.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc320c59b65\",\n" +
                    "        \"outlet_name\"    :       \"Jack&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-1047.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc53797fce3\",\n" +
                    "        \"outlet_name\"    :       \"Tom&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-4.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "},    {\n" +
                    "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc434d785e3\",\n" +
                    "        \"outlet_name\"    :       \"Julia&#039;s outlet\",\n" +
                    "        \"count\"          :       \"-11.00000\",\n" +
                    "        \"reorder_point\"          :       \"\",\n" +
                    "        \"restock_level\"          :       \"\"\n" +
                    "}    ],\n" +
                    "    \"price_book_entries\":    [    {\n" +
                    "        \"id\"                :       \"a6b69a2f-46e0-7595-f936-d910c70354bc\",\n" +
                    "        \"product_id\"        :       \"06bf537b-c7d7-11e7-ff13-0c871ec9808b\",\n" +
                    "        \"price_book_id\"     :       \"06bf537b-c77f-11e7-ff13-0c871e87cd6e\",\n" +
                    "        \"price_book_name\"   :       \"General Price Book (All Products)\",\n" +
                    "        \"type\"              :       \"BASE\",\n" +
                    "        \"outlet_name\"       :       \"\",\n" +
                    "        \"outlet_id\"         :       \"\",\n" +
                    "        \"customer_group_name\" :     \"All Customers\",\n" +
                    "        \"customer_group_id\" :       \"06bf537b-c77f-11e7-ff13-0c871e85cb6d\",\n" +
                    "        \"price\"             :       173.91304,\n" +
                    "        \"loyalty_value\"     :       null,\n" +
                    "        \"tax\"               :       26.08696,\n" +
                    "        \"tax_id\"            :       \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                    "        \"tax_rate\"          :       0.15,\n" +
                    "        \"tax_name\"          :       \"GST\",\n" +
                    "        \"display_retail_price_tax_inclusive\"          :       1,\n" +
                    "        \"min_units\"         :       \"\",\n" +
                    "        \"max_units\"         :       \"\",\n" +
                    "        \"valid_from\"        :       \"\",\n" +
                    "        \"valid_to\"          :       \"\"\n" +
                    "    }],\n" +
                    "\"price\":    173.91304,\n" +
                    "\"tax\":      26.08696,\n" +
                    "\"tax_id\":   \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                    "\"tax_rate\": 0.15,\n" +
                    "\"tax_name\": \"GST\",\n" +
                    "\"taxes\":    [{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbfccecfcbf\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbff716ca41\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc0375f365d\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1b5487984\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1deaff4ca\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc26595c692\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1fd889603\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc27fbd567c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2b69f637e\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2ca699476\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2e1ccdba1\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2f640ca94\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc30beac6d6\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc320c59b65\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc348412b06\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3719f437c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc38db61190\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3abeba818\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3cda4e809\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3eb7216ea\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4150632e9\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc434d785e3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc44ae9f216\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc46908e7fa\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4883fdfb4\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4a384466a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4c11615ae\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4d89fa9e0\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4f56572e8\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc50b2da27a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc51fc83e26\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc53797fce3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc54f679dde\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5722df835\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc59bcffab7\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5bcc27082\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"}],\n" +
                    "\"display_retail_price_tax_inclusive\"          :       1,\n" +
                    "\"updated_at\":       \"2017-03-19 09:33:36\",\n" +
                    "\"deleted_at\":       \"\"\n" +
                    "}\n" +
                    "        ]\n" +
                    "}";
        
        vendTestProduct2 = "{\n" +
                            "        \"products\"    :   [        {\n" +
                            "\"id\":               \"06bf537b-c7d7-11e7-ff13-0c871ec9808b\",\n" +
                            "\"source_id\":        \"\",\n" +
                            "\"variant_source_id\": \"\",\n" +
                            "\"handle\":           \"Sunglasses\",\n" +
                            "\"type\":           \"Fashion\",\n" +
                            "\"has_variants\":   false,\n" +
                            "\"variant_parent_id\":  \"\",\n" +
                            "\"variant_option_one_name\": \"\",\n" +
                            "\"variant_option_one_value\": \"\",\n" +
                            "\"variant_option_two_name\": \"\",\n" +
                            "\"variant_option_two_value\": \"\",\n" +
                            "\"variant_option_three_name\": \"\",\n" +
                            "\"variant_option_three_value\": \"\",\n" +
                            "\"active\":           true,\n" +
                            "\"name\":             \"Sunglasses\",\n" +
                            "\"base_name\":        \"Sunglasses\",\n" +
                            "\"description\":      \"Retro square sunglasses with tinted or polarizing lenses.\",\n" +
                            "\"image\":            \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-thumb.png\",\n" +
                            "\"image_large\":      \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-original.png\",\n" +
                            "\"images\":           [],\n" +
                            "\"sku\":              \"10012\",\n" +
                            "\"tags\":             \"Retro square, Summer\",\n" +
                            "\"brand_id\":         \"06bf537b-c7d7-11e7-ff13-0c871ed74273\",\n" +
                            "\"brand_name\":       \"In The Shade\",\n" +
                            "\"supplier_name\":    \"Ramons\",\n" +
                            "\"supplier_code\":    \"\",\n" +
                            "\"supply_price\":     \"50.00\",\n" +
                            "\"account_code_purchase\":     \"\",\n" +
                            "\"account_code_sales\":        \"\",\n" +
                            "\"track_inventory\":           true,\n" +
                            "\"button_order\":           \"\",\n" +
                            "    \"inventory\": [{\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3eb7216ea\",\n" +
                            "        \"outlet_name\"    :       \"Josh C.&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc0375f365d\",\n" +
                            "        \"outlet_name\"    :       \"Caleb&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\n" +
                            "        \"outlet_name\"    :       \"Main Outlet\",\n" +
                            "        \"count\"          :       \"2.00000\",\n" +
                            "        \"reorder_point\"          :       \"1\",\n" +
                            "        \"restock_level\"          :       \"3\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2ca699476\",\n" +
                            "        \"outlet_name\"    :       \"Hamish&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-5.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc1b5487984\",\n" +
                            "        \"outlet_name\"    :       \"Charlie&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-8.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc46908e7fa\",\n" +
                            "        \"outlet_name\"    :       \"Nic&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc30beac6d6\",\n" +
                            "        \"outlet_name\"    :       \"Isaac&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc50b2da27a\",\n" +
                            "        \"outlet_name\"    :       \"Shaye&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-5.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3719f437c\",\n" +
                            "        \"outlet_name\"    :       \"James&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-23.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc44ae9f216\",\n" +
                            "        \"outlet_name\"    :       \"Kurt&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2f640ca94\",\n" +
                            "        \"outlet_name\"    :       \"Huiyu&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc348412b06\",\n" +
                            "        \"outlet_name\"    :       \"Jacques&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-4.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3abeba818\",\n" +
                            "        \"outlet_name\"    :       \"Jonathan&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4a384466a\",\n" +
                            "        \"outlet_name\"    :       \"Oscar&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1199546.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\n" +
                            "        \"outlet_name\"    :       \"Noel&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-14.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cbfccecfcbf\",\n" +
                            "        \"outlet_name\"    :       \"Annabelle&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2e1ccdba1\",\n" +
                            "        \"outlet_name\"    :       \"Harry&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-5.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc27fbd567c\",\n" +
                            "        \"outlet_name\"    :       \"Elijah&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-7.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc5722df835\",\n" +
                            "        \"outlet_name\"    :       \"William S.&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-25.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc26595c692\",\n" +
                            "        \"outlet_name\"    :       \"Daniel P.&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-163.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2b69f637e\",\n" +
                            "        \"outlet_name\"    :       \"Guy&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc38db61190\",\n" +
                            "        \"outlet_name\"    :       \"Joe&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-5.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc1fd889603\",\n" +
                            "        \"outlet_name\"    :       \"Daniel T.&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc54f679dde\",\n" +
                            "        \"outlet_name\"    :       \"Will&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc59bcffab7\",\n" +
                            "        \"outlet_name\"    :       \"William T.&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4883fdfb4\",\n" +
                            "        \"outlet_name\"    :       \"Oliver&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-13.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4f56572e8\",\n" +
                            "        \"outlet_name\"    :       \"Rory&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-25.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\n" +
                            "        \"outlet_name\"    :       \"Greg&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4150632e9\",\n" +
                            "        \"outlet_name\"    :       \"Josh W.&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-10.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3cda4e809\",\n" +
                            "        \"outlet_name\"    :       \"Jordan&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-7.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cbff716ca41\",\n" +
                            "        \"outlet_name\"    :       \"Ayesha&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc320c59b65\",\n" +
                            "        \"outlet_name\"    :       \"Jack&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1047.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc53797fce3\",\n" +
                            "        \"outlet_name\"    :       \"Tom&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-4.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc434d785e3\",\n" +
                            "        \"outlet_name\"    :       \"Julia&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-11.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "}    ],\n" +
                            "    \"price_book_entries\":    [    {\n" +
                            "        \"id\"                :       \"a6b69a2f-46e0-7595-f936-d910c70354bc\",\n" +
                            "        \"product_id\"        :       \"06bf537b-c7d7-11e7-ff13-0c871ec9808b\",\n" +
                            "        \"price_book_id\"     :       \"06bf537b-c77f-11e7-ff13-0c871e87cd6e\",\n" +
                            "        \"price_book_name\"   :       \"General Price Book (All Products)\",\n" +
                            "        \"type\"              :       \"BASE\",\n" +
                            "        \"outlet_name\"       :       \"\",\n" +
                            "        \"outlet_id\"         :       \"\",\n" +
                            "        \"customer_group_name\" :     \"All Customers\",\n" +
                            "        \"customer_group_id\" :       \"06bf537b-c77f-11e7-ff13-0c871e85cb6d\",\n" +
                            "        \"price\"             :       173.91304,\n" +
                            "        \"loyalty_value\"     :       null,\n" +
                            "        \"tax\"               :       26.08696,\n" +
                            "        \"tax_id\"            :       \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                            "        \"tax_rate\"          :       0.15,\n" +
                            "        \"tax_name\"          :       \"GST\",\n" +
                            "        \"display_retail_price_tax_inclusive\"          :       1,\n" +
                            "        \"min_units\"         :       \"\",\n" +
                            "        \"max_units\"         :       \"\",\n" +
                            "        \"valid_from\"        :       \"\",\n" +
                            "        \"valid_to\"          :       \"\"\n" +
                            "    }],\n" +
                            "\"price\":    173.91304,\n" +
                            "\"tax\":      26.08696,\n" +
                            "\"tax_id\":   \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                            "\"tax_rate\": 0.15,\n" +
                            "\"tax_name\": \"GST\",\n" +
                            "\"taxes\":    [{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbfccecfcbf\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbff716ca41\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc0375f365d\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1b5487984\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1deaff4ca\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc26595c692\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1fd889603\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc27fbd567c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2b69f637e\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2ca699476\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2e1ccdba1\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2f640ca94\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc30beac6d6\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc320c59b65\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc348412b06\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3719f437c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc38db61190\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3abeba818\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3cda4e809\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3eb7216ea\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4150632e9\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc434d785e3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc44ae9f216\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc46908e7fa\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4883fdfb4\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4a384466a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4c11615ae\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4d89fa9e0\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4f56572e8\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc50b2da27a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc51fc83e26\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc53797fce3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc54f679dde\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5722df835\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc59bcffab7\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5bcc27082\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"}],\n" +
                            "\"display_retail_price_tax_inclusive\"          :       1,\n" +
                            "\"updated_at\":       \"2017-03-19 09:33:36\",\n" +
                            "\"deleted_at\":       \"\"\n" +
                            "}\n" +
                            "        ]\n" +
                            "}";
        
        vendTestProduct3 = "{\n" +
                            "        \"products\"    :   [        {\n" +
                            "\"id\":               \"06bf537b-c7d7-11e7-ff13-0c871f89cbbc\",\n" +
                            "\"source_id\":        \"\",\n" +
                            "\"variant_source_id\": \"\",\n" +
                            "\"handle\":           \"DressShirt\",\n" +
                            "\"type\":           \"Fashion\",\n" +
                            "\"has_variants\":   false,\n" +
                            "\"variant_parent_id\":  \"06bf537b-c7d7-11e7-ff13-0c871f6e7517\",\n" +
                            "\"variant_option_one_name\": \"Fabric\",\n" +
                            "\"variant_option_one_value\": \"Cotton\",\n" +
                            "\"variant_option_two_name\": \"Size\",\n" +
                            "\"variant_option_two_value\": \"Medium\",\n" +
                            "\"variant_option_three_name\": \"\",\n" +
                            "\"variant_option_three_value\": \"\",\n" +
                            "\"active\":           true,\n" +
                            "\"name\":             \"Dress Shirt \\/ Cotton \\/ Medium\",\n" +
                            "\"base_name\":        \"Dress Shirt\",\n" +
                            "\"description\":      \"Long sleeved pin stripped shirt.\",\n" +
                            "\"image\":            \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-thumb.png\",\n" +
                            "\"image_large\":      \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-original.png\",\n" +
                            "\"images\":           [],\n" +
                            "\"sku\":              \"10020\",\n" +
                            "\"tags\":             \"\",\n" +
                            "\"brand_id\":         \"06bf537b-c7d7-11e7-ff13-0c871f79ec33\",\n" +
                            "\"brand_name\":       \"Dress to Impress\",\n" +
                            "\"supplier_name\":    \"Lewis Apparel\",\n" +
                            "\"supplier_code\":    \"\",\n" +
                            "\"supply_price\":     \"20.00\",\n" +
                            "\"account_code_purchase\":     \"\",\n" +
                            "\"account_code_sales\":        \"\",\n" +
                            "\"track_inventory\":           true,\n" +
                            "\"button_order\":           \"\",\n" +
                            "    \"inventory\": [{\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\n" +
                            "        \"outlet_name\"    :       \"Greg&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc44ae9f216\",\n" +
                            "        \"outlet_name\"    :       \"Kurt&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\n" +
                            "        \"outlet_name\"    :       \"Noel&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-3.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc1b5487984\",\n" +
                            "        \"outlet_name\"    :       \"Charlie&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4a384466a\",\n" +
                            "        \"outlet_name\"    :       \"Oscar&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-3.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\n" +
                            "        \"outlet_name\"    :       \"Main Outlet\",\n" +
                            "        \"count\"          :       \"10.00000\",\n" +
                            "        \"reorder_point\"          :       \"10\",\n" +
                            "        \"restock_level\"          :       \"5\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc27fbd567c\",\n" +
                            "        \"outlet_name\"    :       \"Elijah&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-19.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc2e1ccdba1\",\n" +
                            "        \"outlet_name\"    :       \"Harry&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "}    ],\n" +
                            "    \"price_book_entries\":    [    {\n" +
                            "        \"id\"                :       \"553fe2aa-272f-35f6-159d-9517cb3afefa\",\n" +
                            "        \"product_id\"        :       \"06bf537b-c7d7-11e7-ff13-0c871f89cbbc\",\n" +
                            "        \"price_book_id\"     :       \"06bf537b-c77f-11e7-ff13-0c871e87cd6e\",\n" +
                            "        \"price_book_name\"   :       \"General Price Book (All Products)\",\n" +
                            "        \"type\"              :       \"BASE\",\n" +
                            "        \"outlet_name\"       :       \"\",\n" +
                            "        \"outlet_id\"         :       \"\",\n" +
                            "        \"customer_group_name\" :     \"All Customers\",\n" +
                            "        \"customer_group_id\" :       \"06bf537b-c77f-11e7-ff13-0c871e85cb6d\",\n" +
                            "        \"price\"             :       78.26087,\n" +
                            "        \"loyalty_value\"     :       null,\n" +
                            "        \"tax\"               :       11.73913,\n" +
                            "        \"tax_id\"            :       \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                            "        \"tax_rate\"          :       0.15,\n" +
                            "        \"tax_name\"          :       \"GST\",\n" +
                            "        \"display_retail_price_tax_inclusive\"          :       1,\n" +
                            "        \"min_units\"         :       \"\",\n" +
                            "        \"max_units\"         :       \"\",\n" +
                            "        \"valid_from\"        :       \"\",\n" +
                            "        \"valid_to\"          :       \"\"\n" +
                            "    }],\n" +
                            "\"price\":    78.26087,\n" +
                            "\"tax\":      11.73913,\n" +
                            "\"tax_id\":   \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                            "\"tax_rate\": 0.15,\n" +
                            "\"tax_name\": \"GST\",\n" +
                            "\"taxes\":    [{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbfccecfcbf\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbff716ca41\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc0375f365d\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1b5487984\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1deaff4ca\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc26595c692\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1fd889603\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc27fbd567c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2b69f637e\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2ca699476\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2e1ccdba1\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2f640ca94\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc30beac6d6\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc320c59b65\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc348412b06\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3719f437c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc38db61190\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3abeba818\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3cda4e809\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3eb7216ea\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4150632e9\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc434d785e3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc44ae9f216\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc46908e7fa\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4883fdfb4\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4a384466a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4c11615ae\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4d89fa9e0\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4f56572e8\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc50b2da27a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc51fc83e26\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc53797fce3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc54f679dde\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5722df835\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc59bcffab7\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5bcc27082\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"}],\n" +
                            "\"display_retail_price_tax_inclusive\"          :       1,\n" +
                            "\"updated_at\":       \"2017-03-19 09:33:36\",\n" +
                            "\"deleted_at\":       \"\"\n" +
                            "}\n" +
                            "        ]\n" +
                            "}";
    
        vendTestProduct4 = "{\n" +
                            "        \"products\"    :   [        {\n" +
                            "\"id\":               \"06bf537b-c7d7-11e7-ff13-0c871f476706\",\n" +
                            "\"source_id\":        \"\",\n" +
                            "\"variant_source_id\": \"\",\n" +
                            "\"handle\":           \"SummerDress\",\n" +
                            "\"type\":           \"Fashion\",\n" +
                            "\"has_variants\":   false,\n" +
                            "\"variant_parent_id\":  \"06bf537b-c7d7-11e7-ff13-0c871f16edb0\",\n" +
                            "\"variant_option_one_name\": \"Size\",\n" +
                            "\"variant_option_one_value\": \"10\",\n" +
                            "\"variant_option_two_name\": \"\",\n" +
                            "\"variant_option_two_value\": \"\",\n" +
                            "\"variant_option_three_name\": \"\",\n" +
                            "\"variant_option_three_value\": \"\",\n" +
                            "\"active\":           true,\n" +
                            "\"name\":             \"Summer Dress \\/ 10\",\n" +
                            "\"base_name\":        \"Summer Dress\",\n" +
                            "\"description\":      \"A light summer floral dress.\",\n" +
                            "\"image\":            \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-thumb.png\",\n" +
                            "\"image_large\":      \"https:\\/\\/info323otago.vendhq.com\\/images\\/placeholder\\/product\\/no-image-white-original.png\",\n" +
                            "\"images\":           [],\n" +
                            "\"sku\":              \"10017\",\n" +
                            "\"tags\":             \"\",\n" +
                            "\"brand_id\":         \"06bf537b-c7d7-11e7-ff13-0c871f227132\",\n" +
                            "\"brand_name\":       \"Summerly\",\n" +
                            "\"supplier_name\":    \"Nomad Fashions\",\n" +
                            "\"supplier_code\":    \"\",\n" +
                            "\"supply_price\":     \"30.00\",\n" +
                            "\"account_code_purchase\":     \"\",\n" +
                            "\"account_code_sales\":        \"\",\n" +
                            "\"track_inventory\":           true,\n" +
                            "\"button_order\":           \"\",\n" +
                            "    \"inventory\": [{\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc4f56572e8\",\n" +
                            "        \"outlet_name\"    :       \"Rory&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc3cda4e809\",\n" +
                            "        \"outlet_name\"    :       \"Jordan&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-9.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\n" +
                            "        \"outlet_name\"    :       \"Greg&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\n" +
                            "        \"outlet_name\"    :       \"Main Outlet\",\n" +
                            "        \"count\"          :       \"3.00000\",\n" +
                            "        \"reorder_point\"          :       \"0\",\n" +
                            "        \"restock_level\"          :       \"0\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\n" +
                            "        \"outlet_name\"    :       \"Noel&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-2.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "},    {\n" +
                            "        \"outlet_id\"      :       \"06bf537b-c7d7-11e7-ff13-2cc320c59b65\",\n" +
                            "        \"outlet_name\"    :       \"Jack&#039;s outlet\",\n" +
                            "        \"count\"          :       \"-1.00000\",\n" +
                            "        \"reorder_point\"          :       \"\",\n" +
                            "        \"restock_level\"          :       \"\"\n" +
                            "}    ],\n" +
                            "    \"price_book_entries\":    [    {\n" +
                            "        \"id\"                :       \"732c125e-512d-2514-bfbf-822ea4841f5e\",\n" +
                            "        \"product_id\"        :       \"06bf537b-c7d7-11e7-ff13-0c871f476706\",\n" +
                            "        \"price_book_id\"     :       \"06bf537b-c77f-11e7-ff13-0c871e87cd6e\",\n" +
                            "        \"price_book_name\"   :       \"General Price Book (All Products)\",\n" +
                            "        \"type\"              :       \"BASE\",\n" +
                            "        \"outlet_name\"       :       \"\",\n" +
                            "        \"outlet_id\"         :       \"\",\n" +
                            "        \"customer_group_name\" :     \"All Customers\",\n" +
                            "        \"customer_group_id\" :       \"06bf537b-c77f-11e7-ff13-0c871e85cb6d\",\n" +
                            "        \"price\"             :       108.69565,\n" +
                            "        \"loyalty_value\"     :       null,\n" +
                            "        \"tax\"               :       16.30435,\n" +
                            "        \"tax_id\"            :       \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                            "        \"tax_rate\"          :       0.15,\n" +
                            "        \"tax_name\"          :       \"GST\",\n" +
                            "        \"display_retail_price_tax_inclusive\"          :       1,\n" +
                            "        \"min_units\"         :       \"\",\n" +
                            "        \"max_units\"         :       \"\",\n" +
                            "        \"valid_from\"        :       \"\",\n" +
                            "        \"valid_to\"          :       \"\"\n" +
                            "    }],\n" +
                            "\"price\":    108.69565,\n" +
                            "\"tax\":      16.30435,\n" +
                            "\"tax_id\":   \"06bf537b-c77f-11e7-ff13-0c871e89e399\",\n" +
                            "\"tax_rate\": 0.15,\n" +
                            "\"tax_name\": \"GST\",\n" +
                            "\"taxes\":    [{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbfccecfcbf\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cbff716ca41\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc0375f365d\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1b5487984\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1deaff4ca\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc26595c692\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc1fd889603\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc27fbd567c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc29c181c0b\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2b69f637e\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2ca699476\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2e1ccdba1\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc2f640ca94\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc30beac6d6\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc320c59b65\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc348412b06\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3719f437c\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc38db61190\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3abeba818\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3cda4e809\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc3eb7216ea\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4150632e9\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc434d785e3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc44ae9f216\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c77f-11e7-ff13-0c871e939ab5\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc46908e7fa\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-22f05c5578ad\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4883fdfb4\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4a384466a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4c11615ae\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4d89fa9e0\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc4f56572e8\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc50b2da27a\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc51fc83e26\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc53797fce3\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc54f679dde\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5722df835\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc59bcffab7\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"},{\"outlet_id\":\"06bf537b-c7d7-11e7-ff13-2cc5bcc27082\",\"tax_id\":\"06bf537b-c77f-11e7-ff13-0c871e89e399\"}],\n" +
                            "\"display_retail_price_tax_inclusive\"          :       1,\n" +
                            "\"updated_at\":       \"2017-03-19 09:33:36\",\n" +
                            "\"deleted_at\":       \"\"\n" +
                            "}\n" +
                            "        ]\n" +
                            "}";
        
        productID1 = "0af7b240-abd7-11e7-eddc-390cbdd83d7f";
        productID2 = "06bf537b-c7d7-11e7-ff13-0c871ec9808b";
        productID3 = "06bf537b-c7d7-11e7-ff13-0c871f89cbbc";
        productID4 = "06bf537b-c7d7-11e7-ff13-0c871f476706";
        
        testSaleProduct = "{\n" +
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
        RouteBuilder routeBuilder = new CustomerUseCouponRB();
        
        // create route interceptor
        routeBuilder.includeRoutes(createInterceptRoutes());
        
        return routeBuilder;
    }
    
    private RouteBuilder createInterceptRoutes() {
        
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            
                // mock vend 1st product id - the coupon
                interceptSendToEndpoint("https4://info323otago.vendhq.com/api/products/" + productID1)
                .skipSendToOriginalEndpoint()
                .log("Mock Get/Put Vend Product ID 1 Called")
                .to("mock:vend-products");
                
                // mock vend 2nd product id
                interceptSendToEndpoint("https4://info323otago.vendhq.com/api/products/" + productID2)
                .skipSendToOriginalEndpoint()
                .log("Mock Get Vend Product ID 2 Called")
                .to("mock:vend-products");
                
                // moock vend 3rd product id
                interceptSendToEndpoint("https4://info323otago.vendhq.com/api/products/" + productID3)
                .skipSendToOriginalEndpoint()
                .log("Mock Get Vend Product ID 3 Called")
                .to("mock:vend-products");                
                
                // mock vend 4th product id
                interceptSendToEndpoint("https4://info323otago.vendhq.com/api/products/" + productID4)
                .skipSendToOriginalEndpoint()
                .log("Mock Get Vend Product ID 4 Called")
                .to("mock:vend-products");                
                
                // create intercept point for coupon product
                interceptSendToEndpoint("http4://localhost:8081/customers/" + couponProductCustomerID
                                                                            + "/coupons/" + 
                                                                            couponIDLoyaltyService)
                .skipSendToOriginalEndpoint()
                .log("Mock Get/Put Coupon from Loyalty Service Called")
                .to("mock:get-coupon-from-rmi");
                
                // add dead endpoint for loyalty service
                from("jms:queue:[CUseCoup] 05-loyalty-service-coupon-update-response").log("${body}");
                from("jms:queue:[CUseCoup] 04_vend-coupon-delete-response").log("${body}");
            } 
        };
    }
    
    private void createGetPutVendProductsMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:vend-products");
	
        // expected messages for 2 routes
        vend.expectedMessageCount(5);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {

                // check first product uri
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                assertStringContains(interceptedURI, "https4://info323otago.vendhq.com/api/products");
                
                // check all of the uris are set with vend authentication
                String auth = exchng.getIn().getHeader("Authorization", String.class);
                assertEquals(auth, "Bearer CjOC4V9CKp10w3EkgLNtR:um8xRZhhaZpRNUXULT");                
                
                // check that body is set to null
                String body = exchng.getIn().getBody(String.class);
                assertNull(body);
                
                // obtain http method
                String httpMethod = exchng.getIn().getHeader("CamelHttpMethod", String.class);
                
                if(httpMethod.equals("GET")) {

                    // check all of the uri's are set with get
                    assertEquals(httpMethod, "GET");
                    
                    if(interceptedURI.contains(productID1)) {
                        assertStringContains(interceptedURI, productID1);
                        exchng.getIn().setBody(vendTestProduct1);                    
                    }
                    else if(interceptedURI.contains(productID2)) {
                        assertStringContains(interceptedURI, productID2);
                        exchng.getIn().setBody(vendTestProduct2);   
                    }
                    else if(interceptedURI.contains(productID3)) {
                        assertStringContains(interceptedURI, productID3);
                        exchng.getIn().setBody(vendTestProduct3);   
                    }
                    else if(interceptedURI.contains(productID4)) {
                        assertStringContains(interceptedURI, productID4);
                        exchng.getIn().setBody(vendTestProduct4);   
                    }
                    else {
                        // temporarily used to terminate unit test if no ID exists
                        System.err.print("INCORRECT URIS");
                        assertEquals(interceptedURI, "NOT WORKING");
                    }

                    System.err.println("Vend Delete Method tested!");                    
                    
                } else if(httpMethod.equals("DELETE")) {
                    
                    // check that it is using vend coupon product id
                    assertStringContains(interceptedURI, productID1);
                    
                    // check all of the uri's are set with get
                    assertEquals(httpMethod, "DELETE");
                    
                    System.err.println("Vend Delete Method tested!");
                }
            }
        });
    }
    
    private void createGetPutLoyaltyServiceCouponMock() {
        
        MockEndpoint vend = getMockEndpoint("mock:get-coupon-from-rmi");
		  
        // expected messages for two routes
        vend.expectedMessageCount(2);
        vend.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchng) throws Exception {
                // check first product uri
                String interceptedURI = exchng.getIn().getHeader("CamelInterceptedEndpoint", String.class);
                assertStringContains(interceptedURI, "http4://localhost:8081/customers/" + couponProductCustomerID
                                                                            + "/coupons/" + 
                                                                            couponIDLoyaltyService);
                
                // check all of the uri's are set with get
                String httpMethod = exchng.getIn().getHeader("CamelHttpMethod", String.class);
                
                if(httpMethod.equals("GET")) {                  
                    
                    assertEquals(httpMethod, "GET");

                    // check content type
                    String contentType = exchng.getIn().getHeader("Content-Type", String.class);
                    assertEquals(contentType, "application/json");

                    // check accept type 
                    String acceptType = exchng.getIn().getHeader("CamelAcceptContentType", String.class);
                    assertEquals(acceptType, "application/json");

                    // check that body is set to null
                    String body = exchng.getIn().getBody(String.class);
                    assertNull(body);

                    // send the coupon object to coupon update route
                    exchng.getIn().setBody(testCoupon);
                    
                    System.err.println("Loyalty Service GET tested!");                    
                    
                } else if(httpMethod.equals("PUT")) {                 
                    
                    assertEquals(httpMethod, "PUT");

                    // check content type
                    String contentType = exchng.getIn().getHeader("Content-Type", String.class);
                    assertEquals(contentType, "application/json");

                    // check accept type 
                    String acceptType = exchng.getIn().getHeader("CamelAcceptContentType", String.class);
                    assertEquals(acceptType, "application/json");
                    
                    // check that the coupon is set to used
                    String couponJSON = exchng.getIn().getBody(String.class);
                    assertStringContains(couponJSON, "true");
                    
                    System.err.println("Loyalty Service PUT tested!");                    
                }
            }
        });
    }
    
    @Test
    public void testCreateCouponRouteInteraction() throws Exception {
        
        ProducerTemplate producer = this.context().createProducerTemplate();
        
        // generate endpoint route mocks
        createGetPutVendProductsMock();
        createGetPutLoyaltyServiceCouponMock();
        
        // send initial payload
        producer.sendBody("jms:queue:vend-for-coupon", testSaleProduct);
        
        assertMockEndpointsSatisfied();
    }    
}