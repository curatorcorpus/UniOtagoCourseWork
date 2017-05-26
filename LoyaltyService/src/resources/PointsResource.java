/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import volatiledao.ClientCouponsVolatileDAO;
import volatiledao.ClientTransactionsVolatileDAO;

/**
 * Class that represents points resource. HTTP GET verb call 
 * depends on the resource URI.
 * 
 * @author curator
 */
@Path("/customers/{customerID}")
public class PointsResource {
    
    private ClientCouponsVolatileDAO coupons = new ClientCouponsVolatileDAO();
    private ClientTransactionsVolatileDAO transactions = new ClientTransactionsVolatileDAO();
    
    private String customerID;
    
    public PointsResource(@PathParam("customerID") String customerID) {
        this.customerID = customerID;
    }
    
    @Path("/points/total")
    @GET
    @Produces("text/plain")
    public Integer getTotalPoints() {
        return transactions.getTotalPoints(customerID);
    }
    
    @Path("/points/unused")
    @GET
    @Produces("text/plain")
    public Integer getUnusedPoints() {
        
       // System.out.println( "transaction points " + transactions.getTotalPoints(customerID) );
        //System.out.println( "coupon points " + coupons.getTotalCouponPoints(customerID) );
        return transactions.getTotalPoints(customerID) - 
               coupons.getTotalCouponPoints(customerID);
    }
}
