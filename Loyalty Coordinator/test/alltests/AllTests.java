/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import routetests.CreateCouponRouteTest;
import routetests.PurchaseRouteTest;
import routetests.UseCouponRouteTest;
import routetests.ViewPointsRouteTest;

/**
 *
 * @author curator
 */
@RunWith(Suite.class)
@SuiteClasses({
    
    CreateCouponRouteTest.class,
    PurchaseRouteTest.class,
    ViewPointsRouteTest.class,
    UseCouponRouteTest.class
        
})
public class AllTests {}


