/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcetests;

import domain.Transaction;
import domain.Transactions;

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
public class TransactionsResourceTests {
    
    private ClientConfig config;
    private Client client;
    
    private String acceptType = "application/json";
    private String transactID = "unitTest";
    
    private Transaction transact;
    private WebTarget transRes;
    
    @Before
    public void setUp() {

        // set up test objects.
        transact = new Transaction(transactID, "test", 1000);
        
        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        
        transRes = 
                client.target("http://localhost:8081/customers/test2/transactions");
        
        // create test transaction
        transRes.request().post(Entity.entity(transact, "application/json"));
    }
    
    @After
    public void tearDown() {
        transRes.request().delete();
    }
    
    @Test
    public void getTranactionsTest() {
        
        // retrieve current transactions.
        Transactions receivedTrans = transRes.request(acceptType)
                                             .get(Transactions.class);
        
        // there should only be the test transaction in the transcations.
        assertEquals(1, receivedTrans.getSize());
        assertEquals(transact, receivedTrans.getById(transactID));
    }
    
    @Test
    public void postTransactionTest() {
        
        String postTestID = "postTest";
        
        // create postTest object.
        Transaction t = new Transaction(postTestID, "test", 1000);
        
        // post test object.
        transRes.request().post(Entity.entity(t, "application/json"));
        
        Transactions receivedTranss = transRes.request().get(Transactions.class);
        
        // there should only be two tranactions.
        assertEquals(2, receivedTranss.getSize());
        assertEquals(t, receivedTranss.getById(postTestID));
        assertEquals(transact, receivedTranss.getById(transactID));
    }
}
