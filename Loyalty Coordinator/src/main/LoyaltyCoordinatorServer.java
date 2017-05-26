/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import routes.CustomerCreateCouponRB;
import routes.CustomerPurchaseRB;
import routes.CustomerUseCouponRB;
import routes.CustomerViewPointsRB;

/**
 *
 * @author curator
 */
public class LoyaltyCoordinatorServer {
 
    public static void main(String[] args) throws Exception {
        // create camel context
        CamelContext camel = new DefaultCamelContext();

        // register ActiveMQ as the JMS Handler.
        ActiveMQConnectionFactory amqFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // add jms component as AMQ
        camel.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(amqFactory));
        
        // trust all classes being used to send serialized domain objects
        amqFactory.setTrustAllPackages(true);
        
        // add routes
        camel.addRoutes(new CustomerPurchaseRB()); // obtains sale object from message
        camel.addRoutes(new CustomerViewPointsRB());
        camel.addRoutes(new CustomerCreateCouponRB()); 
        camel.addRoutes(new CustomerUseCouponRB());
        
        // route config
        camel.setTracing(false);
        camel.setStreamCaching(false);
        
        // start camel server
        System.out.println("Starting Routers ...");
        camel.start();
        System.out.println("Camel Server Ready ...");
    }
}
