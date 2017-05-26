/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import filters.CorsFilter;
import filters.DebugFilter;
import filters.ExceptionLogger;
import filters.ExceptionMessageHandler;

import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.slf4j.bridge.SLF4JBridgeHandler;

import resources.CouponResource;
import resources.CouponsResource;
import resources.PointsResource;
import resources.TransactionResource;
import resources.TransactionsResource;

/**
 * Main RESTful Web Service for running th loyalty service.
 * 
 * @author curator
 */
public class LoyaltyServiceServer {
    
    /**
     * Main Method
     * 
     * @param args console arguments
     * 
     * @throws java.net.URISyntaxException
     */
    public static void main(String args[]) throws URISyntaxException  {
        
        // web service server URI
        String baseUri = "http://localhost:8081/";
        
        // configure the unified logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        
        // create web resource configuration
        ResourceConfig wsConfig = new ResourceConfig();
        
        // register all filters
        wsConfig.register(CorsFilter.class);
        wsConfig.register(DebugFilter.class);
        wsConfig.register(ExceptionLogger.class);
        wsConfig.register(ExceptionMessageHandler.class);
        
        // register all resources
        wsConfig.register(CouponResource.class);
        wsConfig.register(CouponsResource.class);
        wsConfig.register(PointsResource.class);
        wsConfig.register(TransactionResource.class);
        wsConfig.register(TransactionsResource.class);
        
        // bind base uri and start server
    JdkHttpServerFactory.createHttpServer(new URI(baseUri), wsConfig);
        
        // notify start of server
        System.out.println("Service started and listening on " + baseUri);
    }
}
