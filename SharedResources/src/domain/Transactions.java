/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to represent a collection of transaction objects.
 * 
 * @author curator
 */
@XmlRootElement(name = "transactions")
public class Transactions {
    
    @XmlElement(name = "transaction")
    private Map<String, Transaction> transactions; 
    
    public Transactions() {
        this.transactions = new HashMap<>();
    }
    
    public Transactions(Map<String, Transaction> transactions) {
        this.transactions = transactions;
    }
    
    /**
     * Only used for testing. 
     */
    public int getSize() {
        return transactions.size();
    }

    public Integer getTotalPoints() {
        Integer total = 0;
        
        Collection<Transaction> allTransactions = transactions.values();
        
        for(Transaction t : allTransactions) {
            total += t.getPoints();
        }
        
        return total;
    }
    
    public void add(String trans_id, Transaction trans) {
        transactions.put(trans_id, trans);
    }
    
    public void delete(String trans_id) {
        transactions.remove(trans_id);
    }
    
    public boolean exists(String trans_id) {
        return transactions.containsKey(trans_id);
    }
    
    public Transaction getById(String trans_id) {
        return transactions.get(trans_id);
    }

    @Override
    public String toString() {
        return "Transactions{" + "transactions=" + transactions + '}';
    }
}
