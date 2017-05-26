/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import resourcetests.CouponResourceTests;
import resourcetests.CouponsResourceTests;
import resourcetests.PointTests;
import resourcetests.TransactionResourcesTests;
import resourcetests.TransactionsResourceTests;

/**
 *
 * @author curator
 */
@RunWith(Suite.class)
@SuiteClasses({
    
    CouponResourceTests.class,
    CouponsResourceTests.class,
    PointTests.class,
    TransactionResourcesTests.class,
    TransactionsResourceTests.class
        
})
public class AllTests {}
