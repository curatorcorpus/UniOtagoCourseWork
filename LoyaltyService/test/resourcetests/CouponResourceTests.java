/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcetests;

import domain.Coupon;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author curator
 */
public class CouponResourceTests {
    
    private ClientConfig config;
    private Client client;
    
    private String acceptType = "application/json";
    private String customerID = "test2";
    private Integer couponID = 0;
    
    private Coupon coupon = new Coupon(couponID, 9999, false);
    
    private WebTarget coupRes;
    
    @Before
    public void setUp() {

        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        
        coupRes = 
                client.target("http://localhost:8081/customers/test2/coupons/");
        
        // create test coupon
        coupRes.request().post(Entity.entity(coupon, "application/json"));
    }
    
    @After
    public void tearDown() {
        coupRes.path(Integer.toString(couponID)).request().delete();
    }
    
    @Test
    public void getCouponTest() {
        Coupon receivedCoupon = coupRes.path(Integer.toString(couponID))
                                       .request(acceptType)
                                       .get(Coupon.class);
        
        assertEquals(receivedCoupon, coupon);
    }
    
    @Test(expected = javax.ws.rs.NotFoundException.class)
    public void deleteCouponTest() {
        
        Integer deleteTestID = 1111;
        Coupon c = new Coupon(deleteTestID, 9999, false);
        
        
        coupRes.request().post(Entity.entity(c, "application/json"));
        Coupon receivedCoupon = coupRes.path(Integer.toString(deleteTestID))
                                       .request(acceptType)
                                       .get(Coupon.class);
        
        // test that delete test coupon successfully posted.
        assertEquals(c, receivedCoupon);
        
        coupRes.path(Integer.toString(couponID)).request().delete();
        
        // invoke NotFoundException.
        c = coupRes.path(Integer.toString(couponID)).request().get(Coupon.class);
    }
    
    @Test
    public void putCouponTest() {
        
        Coupon testCoupon = new Coupon(couponID, 70000, false);
        
        coupRes.path(couponID.toString())
               .request()
               .put(Entity.entity(testCoupon, "application/json"));
        
        Coupon receivedCoup = coupRes.path(couponID.toString())
                                     .request()
                                     .get(Coupon.class);
        
        assertEquals(testCoupon, receivedCoup);
        
        // put the same test coupon object.
        coupRes.path(couponID.toString())
               .request()
               .put(Entity.entity(coupon, "application/json"));
    }
}
