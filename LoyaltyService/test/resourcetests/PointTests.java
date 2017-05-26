/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcetests;

import domain.Coupon;
import domain.Transaction;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import volatiledao.ClientCouponsVolatileDAO;
import volatiledao.ClientTransactionsVolatileDAO;

/**
 *
 * @author curator
 */
public class PointTests {
    
    private ClientCouponsVolatileDAO coupDAO;
    private ClientTransactionsVolatileDAO transDAO;
    
    private Client client;
    private ClientConfig config;
    private WebTarget res;
    
    private String customerID = "test2";
    
    private Coupon c1, c2, c3;
    private Transaction t1, t2, t3;
    
    @Before
    public void setUp() {
        
        // construct daos
        coupDAO = new ClientCouponsVolatileDAO();
        transDAO = new ClientTransactionsVolatileDAO();
        
        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        
        res = client.target("http://localhost:8081/customers/test2/");
        
        // create coupons and transcations
        c1 = new Coupon(1111, 500, false);
        c2 = new Coupon(2222, 500, false);
        c3 = new Coupon(3333, 500, false);
        
        t1 = new Transaction("t1", "test_shop", 1000);
        t2 = new Transaction("t2", "test_shop", 1000);
        t3 = new Transaction("t3", "test_shop", 1000);
        
        // post all test objects.
        res.path("coupons").request().post(Entity.entity(c1, "application/json"));
        res.path("coupons").request().post(Entity.entity(c2, "application/json"));
        res.path("coupons").request().post(Entity.entity(c3, "application/json"));
        res.path("transactions").request().post(Entity.entity(t1, "application/json"));
        res.path("transactions").request().post(Entity.entity(t2, "application/json"));
        res.path("transactions").request().post(Entity.entity(t3, "application/json"));
    }
    
    @After
    public void tearDown() {
        res.path("coupons").request().delete();
        res.path("transactions").request().delete();
    }
    
    @Test
    public void totalPointsTest() {
        Integer totalPoints = res.path("points/total").request().get(Integer.class);
        
        // the results should be 1000 * 3 = 3000 points.
        assertEquals(3000, totalPoints.intValue());
    }
    
    @Test
    public void unusedPointsTest() {
        Integer unusedPoints = res.path("points/unused").request().get(Integer.class);
        
        // the results should be 3000 - (500 * 3) = 1500.
        assertEquals(1500, unusedPoints.intValue());
    }
}
