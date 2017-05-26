/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volatiledao;

import domain.Transaction;
import domain.Transactions;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotFoundException;

/**
 * DAO which maps customers via ID to a collection of transactions. 
 * 
 * @author curator
 */
public class ClientTransactionsVolatileDAO {
    
    /**
     * Field for mapping customers to a collection of transactions.
     */
    private static Map<String, Transactions> clTransactions = new HashMap<>();
    
    /**
     * Default constructor.
     */
    public ClientTransactionsVolatileDAO() {
        if(clTransactions.isEmpty()) {
           Transactions s = new Transactions();
           Transactions t = new Transactions();
           Transactions u = new Transactions();
           
           s.add("test_t", new Transaction("test_t", "test_shop", 2000));
           t.add("borris", new Transaction("borris", "dummy transaction", 10000));
           u.add("dorris", new Transaction("dorris", "dummy transaction", 10000));
           
           clTransactions.put("test", s);
           clTransactions.put("06bf537b-c7d7-11e7-ff13-2d957f9ff0f0", t);
           clTransactions.put("06bf537b-c7d7-11e7-ff13-2d958c8879b7", u);
        }
    }
    
    /**
     * Method for adding a single transaction to dao.
     * 
     * @param customerID
     * @param transact - new transaction.
     */
    public void addTransaction(String customerID, Transaction transact) {

        // check if collection set of clTransactions under customer id exists.
        if(clTransactions.containsKey(customerID)) {
            Transactions existingTranSet = clTransactions.get(customerID);
            existingTranSet.add(transact.getId(), transact);

            clTransactions.put(customerID, existingTranSet);

        // otherwise create new customer id mapping.
        } else {
            Transactions newTranMap = new Transactions();
            newTranMap.add(transact.getId(), transact);

            clTransactions.put(customerID, newTranMap);
        }
    }

    /**
     * Method that returns a transaction associated with transaction id.
     * 
     * @param customerID
     * @param transID
     * @return a transaction.
     */
    public Transaction getTransactionById(String customerID, String transID) {
        // check if the customer exists.
        if(clTransactions.containsKey(customerID)) {
            Transactions existingTrans = clTransactions.get(customerID);

            // check if transaction exists.
            if(existingTrans.exists(transID)) {
                return existingTrans.getById(transID);
            } else {
                throw new NotFoundException("There is no transaction that matches the ID.");
            }
        } else {
            throw new NotFoundException("There is no customer that matches the ID.");
        }
    }

    public Transactions getTransactions(String customerID) {
        return clTransactions.get(customerID);
    }

    /**
     * Method for deleting a single transaction.
     * 
     * @param customerID
     * @param transID 
     */
    public void deleteTransaction(String customerID, String transID) {
        // check if customer exists.
        if(clTransactions.containsKey(customerID)) {
            Transactions existingTrans = clTransactions.get(customerID);

            // check if transaction exists.
            if(existingTrans.exists(transID)) {
                existingTrans.delete(transID);
            } else {
                throw new NotFoundException("There is no transaction that matches the ID.");
            }
        } else {
            throw new NotFoundException("There is no customer that matches the ID.");
        }
    }
    
    public Integer getTotalPoints(String customerID) {
        if(!clTransactions.containsKey(customerID)) {
            return -99999;
        }
        
        return clTransactions.get(customerID).getTotalPoints();
    }
    
    /**
     * Only for testing. Deletes entire collection of transactions.
     */
    public void deleteTransactions(String customerID) {
        clTransactions.remove(customerID);
    }
}
