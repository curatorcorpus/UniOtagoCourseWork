/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import domain.Transaction;
import domain.Transactions;

import javax.inject.Inject;
import javax.ws.rs.DELETE;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import volatiledao.ClientTransactionsVolatileDAO;

/**
 * Class that represent transactions resource.
 * 
 * @author curator
 */
@Path("customers/{customerID}/transactions")
public class TransactionsResource {
	
    private ClientTransactionsVolatileDAO dao = new ClientTransactionsVolatileDAO();
    
    private String customerID;
    
    public TransactionsResource(@PathParam("customerID") String customerID) {
        this.customerID = customerID;
    }
   
    @GET
    public Transactions getTransactions() {
        return dao.getTransactions(customerID);
    }

    @POST
    public void createTransaction(Transaction transact) {
        dao.addTransaction(customerID, transact);
    }
    
    /**
     * Only for Testing purposes.
     */
    @DELETE
    public void deleteTransactions() {
        dao.deleteTransactions(customerID);
    }
}
