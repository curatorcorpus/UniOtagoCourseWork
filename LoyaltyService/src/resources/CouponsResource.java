/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import domain.Coupon;
import domain.Coupons;

import javax.ws.rs.DELETE;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import volatiledao.ClientCouponsVolatileDAO;

/**
 * Class that represent coupons resource.
 * 
 * @author curator
 */
@Path("customers/{customerID}/coupons")
public class CouponsResource {
	
    private ClientCouponsVolatileDAO dao = new ClientCouponsVolatileDAO();
    
    private String customerID;
    
    public CouponsResource(@PathParam("customerID") String customerID) {
        this.customerID = customerID;
    }
   
    @GET
    public Coupons getCoupons() {
        return dao.getCoupons(customerID);
    }

    @POST
    public Integer createCoupon(Coupon coupon) {
        return dao.addCoupon(customerID, coupon);
    }
    
    /**
     * Only for testing.
     */
    @DELETE
    public void deleteCoupons() {
        dao.deleteCoupons(customerID);
    }
}
