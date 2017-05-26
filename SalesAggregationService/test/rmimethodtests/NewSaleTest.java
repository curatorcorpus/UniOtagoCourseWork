package rmimethodtests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import domain.Customer;
import domain.Sale;
import domain.SaleItem;

import remoteinterface.ISaleAggregation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author curator
 */
public class NewSaleTest {
    
    private Registry reg;
    private ISaleAggregation server; 
    
    private Sale testSale;
    
    @Before
    public void setUp() throws RemoteException, NotBoundException {
        String localAddress = "localhost";
        
        reg = LocateRegistry.getRegistry(localAddress);
        
        // obtain reference to server
        server = (ISaleAggregation) reg.lookup("SAS");
        
        Collection<SaleItem> saleItems = new ArrayList<>();
        
        Customer testCustomer = new Customer('M', "1/1/2000");
        SaleItem testSaleItem= new SaleItem("testID", 9999.0, 100.0);
        
        saleItems.add(testSaleItem);
        
        // create test sale.
        testSale = new Sale(saleItems, testCustomer, "1/1/2010");
    }
    
    @After
    public void tearDown() throws RemoteException {
        server.deleteTestSale(testSale);
    }
    
    @Test
    public void newSaleTest() throws RemoteException {
        
        server.newSale(testSale);
        
        // there should be only one saleitem in the returned sale.
        Collection<Sale> receivedSales = server.getSale();
        
        // check that there is only 1 sale item
        assertEquals(1, receivedSales.size());
        
        // check if saleitem object is the same.
        for(Sale s : receivedSales) {
            assertEquals(testSale, s);
        }
    }
    
}
