/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remoteinterface;

import domain.Sale;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.Collection;

/**
 *
 * @author curator
 */
public interface ISaleAggregation extends Remote {
 
    /**
     * Sends a sale item to the server to store.
     * 
     * @param sale 
     */
    public void newSale(Sale sale) throws RemoteException;
 
    /**
     * For testing only.
     * 
     * @return 
     */
    public Collection<Sale> getSale() throws RemoteException;
    
    
   /**
    * Only for testing.
    */
    public void deleteTestSale(Sale sale) throws RemoteException;
}
