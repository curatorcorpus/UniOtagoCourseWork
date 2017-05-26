/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to represent a transaction object.
 * 
 * @author curator
 */
@XmlRootElement
public class Transaction implements Serializable {
    
    private String  id;
    private String  shop;
    private Integer points;
    
    public Transaction() {
        this.id = "-1";
        this.shop = "-1";
        this.points = -1;
    }

    public Transaction(String id, String shop, Integer points) {
        this.id = id;
        this.shop = shop;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.id);
        hash = 13 * hash + Objects.hashCode(this.shop);
        hash = 13 * hash + Objects.hashCode(this.points);
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
        final Transaction other = (Transaction) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.shop, other.shop)) {
            return false;
        }
        if (!Objects.equals(this.points, other.points)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + 
                                ", shop=" + shop + 
                                ", points=" + points + '}';
    }
}
