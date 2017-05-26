/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import domain.Transaction;

import javax.inject.Inject;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import volatiledao.ClientTransactionsVolatileDAO;

/**
 * Class that represents a transaction resource.
 * 
 * @author curator
 */
@Path("/customers/{customerID}/transactions/{transactionID}")
public class TransactionResource {
 
    private ClientTransactionsVolatileDAO dao = 
				            new ClientTransactionsVolatileDAO(); 
    
    private String customerID;
    private String transID;
    
    public TransactionResource(@PathParam("customerID") String customerID,
                               @PathParam("transactionID") String transID) {
        
        this.customerID = customerID;
        this.transID = transID;
    }
    
    @GET
    public Transaction getTransaction() {
        return dao.getTransactionById(customerID, transID); 
    }

    @DELETE
    public void deleteTransaction() {	 
	dao.deleteTransaction(customerID, transID);
    }
}
