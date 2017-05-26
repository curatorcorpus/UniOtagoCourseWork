/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import implementations.SaleAggregationImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main class for running the sales aggregation service.
 * 
 * @author curator
 */
public class SalesAggregationService {
    
    /**
     * Main Method.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException {
        
        // create server with impl.
        SaleAggregationImpl server = new SaleAggregationImpl();
        
        // create a RMI Registry port 1099.
        Registry rgsty = LocateRegistry.createRegistry(1099);
        
        // reference server to the new registry.
        rgsty.rebind("SAS", server);
        
        // notify.
        System.out.println("Sales Aggregation Service Started on 1099 ...");
    }
    
}
