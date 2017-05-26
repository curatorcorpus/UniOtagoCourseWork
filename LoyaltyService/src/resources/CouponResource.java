/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import domain.Coupon;

import javax.inject.Inject;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import volatiledao.ClientCouponsVolatileDAO;

/**
 * Class that represents a coupon resource.
 * 
 * @author curator
 */
@Path("/customers/{customerID}/coupons/{couponID}")
public class CouponResource {
    
    private ClientCouponsVolatileDAO dao = new ClientCouponsVolatileDAO(); 
    
    private String customerID;
    private Integer coupID;
    
    public CouponResource(@PathParam("customerID") String customerID,
                          @PathParam("couponID") Integer coupID) {
        
        this.customerID = customerID;
        this.coupID = coupID;
    }
    
    @GET
    @Produces("application/json")
    public Coupon getCoupon() {
        return dao.getCouponById(customerID, coupID);
    }

    @DELETE
    public void deleteCoupon() {	 
	dao.deleteCoupon(customerID, coupID);
    }
    
    @PUT
    public void updateCoupon(Coupon coupon) {
        dao.updateCoupon(customerID, coupon.getId(), coupon);
    }
}
