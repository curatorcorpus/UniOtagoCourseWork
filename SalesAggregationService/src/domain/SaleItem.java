/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class that represents a saleitem.
 * 
 * @author curator
 */
public class SaleItem implements Serializable {
    
    @SerializedName("product_id")
    private String productID;
    
    @SerializedName("quantity")
    private Double quantity;
    
    @SerializedName("price")
    private Double price;
    
    public SaleItem() {}

    public SaleItem(String productID, Double quantity, Double price) {
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.productID);
        hash = 79 * hash + Objects.hashCode(this.quantity);
        hash = 79 * hash + Objects.hashCode(this.price);
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
        final SaleItem other = (SaleItem) obj;
        if (!Objects.equals(this.productID, other.productID)) {
            return false;
        }
        if (!Objects.equals(this.quantity, other.quantity)) {
            return false;
        }
        if (!Objects.equals(this.price, other.price)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SaleItem{" + "productID=" + productID + ", quantity=" + quantity + ", price=" + price + '}';
    }
}
