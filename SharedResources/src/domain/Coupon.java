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
 * Class to represent a coupon object.
 * 
 * @author curator
 */
@XmlRootElement
public class Coupon implements Serializable {
    
    private Integer id;
    private Integer points;
    private Boolean used;

    public Coupon() {}
    
    public Coupon(Integer id, Integer points, Boolean used) {
        this.id = id;
        this.points = points;
        this.used = used;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean isUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.points);
        hash = 83 * hash + Objects.hashCode(this.used);
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
        final Coupon other = (Coupon) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.points, other.points)) {
            return false;
        }
        if (!Objects.equals(this.used, other.used)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Coupon{" + "id=" + id + ", points=" + points + ", used=" + used + '}';
    }
}
