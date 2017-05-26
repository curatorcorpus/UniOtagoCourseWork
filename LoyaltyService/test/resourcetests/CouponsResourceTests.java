/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcetests;

import domain.Coupon;
import domain.Coupons;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author curator
 */
public class CouponsResourceTests {
    private ClientConfig config;
    private Client client;
    
    private String acceptType = "application/json";
    private Integer couponID = 0;
    
    private Coupon coupon;
    private WebTarget couponRes;
    
    @Before
    public void setUp() {

        // set up test objects.
        coupon = new Coupon(couponID, 1000, false);
        
        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        
        couponRes = 
                client.target("http://localhost:8081/customers/test2/coupons");
        
        // create test coupon.
        couponRes.request().post(Entity.entity(coupon, "application/json"));
    }
    
    @After
    public void tearDown() {
        couponRes.request().delete();
    }
    
    @Test
    public void getCouponsTest() {
        
        // retrieve current coupons.
        Coupons receivedCoupon = couponRes.request(acceptType)
                                          .get(Coupons.class);
        
        // there should only be the test couponion in the transcations.
        assertEquals(1, receivedCoupon.getSize());
        //assertEquals();
        assertEquals(coupon, receivedCoupon.getById(couponID));
    }
    
    @Test
    public void postCouponTest() {
        
        Integer postTestID = 1111;
        
        // create postTest object.
        Coupon c = new Coupon(postTestID, 9999, false);
        
        // post test object.
        couponRes.request().post(Entity.entity(c, "application/json"));
        
        Coupons receivedCoupons = couponRes.request().get(Coupons.class);
        
        // there should only be two tranactions.
        assertEquals(2, receivedCoupons.getSize());
        assertEquals(c, receivedCoupons.getById(postTestID));
        assertEquals(coupon, receivedCoupons.getById(couponID));
        
        // delete post test coupon.
        couponRes.request(Integer.toString(postTestID)).delete();
    }
    
}
