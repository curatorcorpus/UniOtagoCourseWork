/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to represent a collection of coupon objects.
 * 
 * @author curator
 */
@XmlRootElement(name = "coupons")
public class Coupons implements Serializable {
    
    @XmlElement(name = "coupons")
    private Map<Integer, Coupon> coupons; 
    
    public Coupons() {
        this.coupons = new HashMap<>();
    }
    
    public Coupons(Map<Integer, Coupon> coupons) {
        this.coupons = coupons;
    }
    
    public void add(Integer couponID, Coupon coupon) {
        coupons.put(couponID, coupon);
    }
    
    public void delete(Integer couponID) {
        coupons.remove(couponID);
    }
    
    public boolean exists(Integer couponID) {
        return coupons.containsKey(couponID);
    }
    
    public Coupon getById(Integer couponID) {
        return coupons.get(couponID);
    }

    public int getSize() {
        return coupons.size();
    }
    
    public Integer getTotalCouponPoints() {
        
        Integer total = 0;
        
        Collection<Coupon> allCoupons = coupons.values();
        
        for(Coupon c : allCoupons) {
            total += c.getPoints();
        }
        
        return total;
    }
    
    public void update(Integer couponID, Coupon coupon) {
        coupons.replace(couponID, coupon);
    }
    
    @Override
    public String toString() {
        return "Coupons{" + "coupons=" + coupons + '}';
    }
}
