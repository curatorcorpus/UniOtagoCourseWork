/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcetests;

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

import volatiledao.ClientTransactionsVolatileDAO;

/**
 *
 * @author curator
 */
public class TransactionResourcesTests {
    
    private ClientConfig config;
    private Client client;
    
    private String acceptType = "application/json";
    private String customerID = "test";
    private String transactID = "unitTest";
    
    private Transaction transact = new Transaction(transactID, "test", 1000);
    
    private WebTarget transRes;
    
    @Before
    public void setUp() {

        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        
        transRes = 
                client.target("http://localhost:8081/customers/test/transactions/");
        
        // create test transaction
        transRes.request().post(Entity.entity(transact, "application/json"));
    }
    
    @After
    public void tearDown() {
        transRes.path(transactID).request().delete();
    }
    
    @Test
    public void getTranactionTest() {
        Transaction receivedTrans = transRes.path(transactID)
                                            .request(acceptType)
                                            .get(Transaction.class);
        
        assertEquals(receivedTrans, transact);
    }
    
    @Test(expected = javax.ws.rs.NotFoundException.class)
    public void deleteTransactionTest() {
        
        String deleteTestID = "deleteTest";
        Transaction t = new Transaction(deleteTestID, "test", 1000);
        
        
        transRes.request().post(Entity.entity(t, "application/json"));
        Transaction receivedTrans = transRes.path(deleteTestID)
                                            .request(acceptType)
                                            .get(Transaction.class);
        
        // test that delete test transaction successfully posted.
        assertEquals(t, receivedTrans);
        
        transRes.path(deleteTestID).request().delete();
        
        // invoke NotFoundException.
        t = transRes.path(deleteTestID).request().get(Transaction.class);
    }
}
