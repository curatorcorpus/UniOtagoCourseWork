/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volatiledao;

import domain.Coupon;
import domain.Coupons;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotFoundException;

/**
 * DAO which maps customers via ID to a collection of coupons. 
 * 
 * @author curator
 */
public class ClientCouponsVolatileDAO {
    
    /**
     * field for mapping customers to a collection of coupons.
     */
    private static Map<String, Coupons> clCoupons = new HashMap<>();
    
    /**
     * Default constructor.
     */
    public ClientCouponsVolatileDAO() {
        if(clCoupons.isEmpty()) {
            Coupons s = new Coupons();
            Coupons t = new Coupons();
            Coupons u = new Coupons();

            s.add(1000, new Coupon(1000, 1000, false));
            t.add(-1000, new Coupon(-1000, 2000, false));
            u.add(-2000, new Coupon(-2000, 2000, false));

            clCoupons.put("test", s);
            clCoupons.put("06bf537b-c7d7-11e7-ff13-2d957f9ff0f0", t);
            clCoupons.put("06bf537b-c7d7-11e7-ff13-2d958c8879b7", u);
        }
    }
	
    /**
     * Method for adding a single coupon.
     * 
     * @param customerID
     * @param coupon 
     */
    public Integer addCoupon(String customerID, Coupon coupon) {

        int couponID = clCoupons.size() + 1000;
        
        // set system coupon id
        coupon.setId(couponID);
        
        // check if collection set of clCoupons under customer id exists.
        if(clCoupons.containsKey(customerID)) {
            Coupons existingCopuMap = clCoupons.get(customerID);
            
            existingCopuMap.add(couponID, coupon);

            clCoupons.put(customerID, existingCopuMap);
        
        // otherwise create new customer id mapping to new coupon collection.
        } else {
            Coupons newCoupMap = new Coupons();
            
            newCoupMap.add(couponID, coupon);

            clCoupons.put(customerID, newCoupMap);
        }
        
        return couponID;
    }

    /**
     * Method that returns a coupon associated with coupon id.
     * 
     * @param customerID
     * @param couponID
     * @return a single coupon.
     */
    public Coupon getCouponById(String customerID, Integer couponID) {
        // check if the customer exists.
        if(clCoupons.containsKey(customerID)) {
            Coupons existingCoupons = clCoupons.get(customerID);

            // check if coupon exists.
            if(existingCoupons.exists(couponID)) {
                return existingCoupons.getById(couponID);
            } else {
                throw new NotFoundException("There is no coupon that matches the ID.");
            }
        } else {
            throw new NotFoundException("There is no customer that matches the ID.");
        }
    }

    public Coupons getCoupons(String customerID) {
        return clCoupons.get(customerID);
    }

    public Integer getTotalCouponPoints(String customerID) {
        if(!clCoupons.containsKey(customerID)) {
            return -9;
        }
        
        return clCoupons.get(customerID).getTotalCouponPoints();
    }
    
    /**
     * Method for deleting a coupon transaction.
     * 
     * @param customerID
     * @param couponID 
     */
    public void deleteCoupon(String customerID, Integer couponID) {
        // check if customer exists.
        if(clCoupons.containsKey(customerID)) {
            Coupons existingCoupons = clCoupons.get(customerID);

            // check if coupons exists.
            if(existingCoupons.exists(couponID)) {
                existingCoupons.delete(couponID);
            } else {
                throw new NotFoundException("There is no coupons that matches the ID.");
            }
        } else {
            throw new NotFoundException("There is no customer that matches the ID.");
        }
    }
    
    /**
     * Method used for updating an existing coupon.
     * 
     * @param customerID
     * @param couponID
     * @param coupon 
     */
    public void updateCoupon(String customerID, Integer couponID, Coupon coupon) {
        // check if customer exists.
        if(clCoupons.containsKey(customerID)) {
            Coupons existingCoupons = clCoupons.get(customerID);

            // check if coupons exists.
            if(existingCoupons.exists(couponID)) {
                existingCoupons.update(couponID, coupon);
            } else {
                throw new NotFoundException("There is no coupons that matches the ID.");
            }
        } else {
            throw new NotFoundException("There is no customer that matches the ID.");
        }
    }
    
    /**
     * Only used for testing. Deletes all coupons associated with customer id.
     */
    public void deleteCoupons(String customerID) {
        clCoupons.remove(customerID);
    }
}
