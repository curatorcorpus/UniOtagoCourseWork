/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementations;

import remoteinterface.ISaleAggregation;

import domain.Sale;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of the server interface.
 *
 * @author curator
 */
public class SaleAggregationImpl extends UnicastRemoteObject implements ISaleAggregation {
    
    private static Collection<Sale> sales = new ArrayList<>();

    public SaleAggregationImpl() throws RemoteException {}
    
    @Override
    public void newSale(Sale sale) {
        System.err.println("[DEBUG SA_IMPL]: Adding A New Sale at " + sale.getDate());
        System.out.println("[DEBUG SA_IMPL]: Sale: " + sale);
        
        sales.add(sale);
    }

    /**
     * For testing only.
     * 
     * @return 
     */
    @Override
    public Collection<Sale> getSale() {
        return sales;
    }

    /**
     * For Testing only.
     * @param sale
     * @throws RemoteException 
     */
    @Override
    public void deleteTestSale(Sale sale) throws RemoteException {
        sales.remove(sale);
    }
    
    
}
