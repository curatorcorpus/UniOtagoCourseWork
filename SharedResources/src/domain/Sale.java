/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import java.util.Collection;
import java.util.Objects;

/**
 * Class that represents a sale object.
 * 
 * @author curator
 */
public class Sale implements Serializable {
    
    @SerializedName("register_sale_products")
    private Collection<SaleItem> saleItems;
    
    private Customer customer;
    
    @SerializedName("sale_date")
    private String date;

    public Sale() {}
    
    public Sale(Collection<SaleItem> saleItems, Customer customer, String date) {
        // check if sale item list is not empty. must have at least one sale.
        if(!saleItems.isEmpty()) {
            this.saleItems = saleItems;
            this.customer = customer;
            this.date = date;
        } else {
            System.err.println("Sale not created, must have at least one sale item.");
        }
    }

    public Collection<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(Collection<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.saleItems);
        hash = 23 * hash + Objects.hashCode(this.customer);
        hash = 23 * hash + Objects.hashCode(this.date);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sale other = (Sale) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.saleItems, other.saleItems)) {
            return false;
        }
        if (!Objects.equals(this.customer, other.customer)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Sale{" + "saleItems=" + saleItems + ", customer=" + customer + ", date=" + date + '}';
    }
}
