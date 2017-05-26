/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('ajaxClient', ['ngResource']);

app.controller('ajaxClientController', function($scope, $resource) 
{
    $scope.restUri = "http://localhost\\:8081/customers/"; 

    $scope.avail_points = 0;
    $scope.pts_to_use   = 0;
    $scope.dollar_equiv = 0;
    
    $scope.cust_email = "";
    $scope.cust_id    = "";
    $scope.cust_name  = ""; 
    
    $scope.coupon_id = "";

// ============================= SOCKETS ===================================    
    $scope.initializeSocket = function() 
    {
        var uri = "ws://localhost:9002/loyalty_coordinator";

        ws = new WebSocket(uri);

        // method to call when message arrives from server
        ws.onmessage = function(event) 
        {
            var json_data = event.data;
            var cust_details = JSON.parse(json_data);
            
            try {
                $scope.cust_id = cust_details.customers[0].id;
                $scope.cust_name  = cust_details.customers[0].name;
                $scope.cust_email = cust_details.customers[0].email;
            } catch(e) {
                console.log(e);
                alert("Email Doesn't Exist");
                return;
            }

            // points obtained after cust_id is provided
            var coupRes = $resource($scope.restUri + ":rewards_id/points/unused", null, 
            {
                unusedPts: {
                    method: 'GET',
                    isArray: false,
                    headers: {
                        'Accept': 'text/plain'
                    },
                    transformResponse: 
		        function(data, headersGetter, status) {
                            return {content: data};
	                }
                    }
            });
            
            // gets points, async call - callback.
            coupRes.unusedPts({rewards_id: $scope.cust_id}, 
                
                // callback
                function(points) {
                    $scope.avail_points = points.content;
                }
            );
    
            $scope.$apply();
        };          

        ws.onopen = function(event)
        {
            console.log("** Connected to Loyalty Coordinator **");
        };
        ws.onerror = function(event)
        {
            console.log("** Error: " + event.data + " **");
        };
        ws.onclose = function(event)
        {
            console.log("** Connection to server has been closed **");
        };
    };

    // websocket for creating new coupon products
    $scope.initializeSocket4Coupon = function() 
    {
        var uri = "ws://localhost:9002/loyalty_coordinator/createcoupon";

        wsCC = new WebSocket(uri);

        // method to call when message arrives from server
        wsCC.onmessage = function(event) 
        {
            // assign value to coupon
            $scope.coupon_value += $scope.dollar_equiv;
            
            // generate barcode
            var canvas = document.getElementById("barcode");
            JsBarcode(canvas, event.data,  {
                width: 1
            });
        };          

        wsCC.onopen = function(event)
        {
            console.log("** Connected to Loyalty Coordinator for Coupons**");
        };
        wsCC.onerror = function(event)
        {
            console.log("** Error: " + event.data + " **");
        };
        wsCC.onclose = function(event)
        {
            console.log("** Connection to server has been closed **");
        };
    };

// ============================================================================================
    
    // for customer login
    $scope.sendEmail = function() 
    {
        input_email = document.getElementById("input_email").value;

        if(input_email === "") {
            console.log("no email provided");
        } else {
            ws.send(input_email);
        }
    };
    
    // for creating new coupon
    $scope.sendNewCoupon = function()
    {
        // check customer id exists
        if($scope.cust_id !== "") 
        {
            // create coupon JSON object
            var newCoupon = {
                          "id": -1,
                          "points": $scope.pts_to_use,
                          "used": false
                         };
            
            // create resource call for creating new coupon
            var coupRes = $resource($scope.restUri + $scope.cust_id + "/coupons", null, 
            {
                createCoupon: {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    transformResponse: 
                        function(data, headersGetter, status) {
                            return {content: data};
			},
                    isArray: false
                }
            });

            // send and make callback receive response.
            coupRes.createCoupon(newCoupon,
            
                // callback
                function(c_identity) {
                    $scope.coupon_id = c_identity.content;
                     
                    // prepare json for new vend product
                    var newVendProduct = {
                                            "source_id": $scope.coupon_id,
                                            "source_variant_id": "",
                                            "handle": $scope.cust_id,
                                            "type": "Coupon",
                                            "tags": "loyalty",
                                            "name": "Coupon For " + $scope.cust_name,
                                            "description": "Discounts Price with Loyalty Points",
                                            "sku": "",
                                            "variant_option_one_name": "",
                                            "variant_option_one_value": "",
                                            "variant_option_two_name": "",
                                            "variant_option_two_value": "",
                                            "variant_option_three_name": "",
                                            "variant_option_three_value": "",
                                            "supply_price": "",
                                            "retail_price": -$scope.dollar_equiv,
                                            "tax": "GST",
                                            "brand_name": "",
                                            "supplier_name": "",
                                            "supplier_code": "",
                                            "inventory": [
                                            {
                                                "outlet_name": "",
                                                "count": 0,
                                                "reorder_point": 0,
                                                "restock_level": 0
                                            }]
                                        };
                                        
                    // make it easier for myself and convert to json
                    wsCC.send(JSON.stringify(newVendProduct));
                }
            );
        }
    };
    
    $scope.calcEquivDollar = function()
    {
        if($scope.cust_id !== "") 
        {
            if($scope.pts_to_use > parseInt($scope.avail_points)) 
            {
                alert("Exceeds Available Points!");
            } else
            {
                $scope.dollar_equiv = $scope.pts_to_use / 10.0;
            }
        }
    };
    
    // reset input fields.
    $scope.resetFields = function()
    {
        $scope.avail_points = 0;
        $scope.pts_to_use   = 0;
        $scope.dollar_equiv = 0;

        $scope.cust_id    = "";
        $scope.cust_name  = ""; 
    };
    
    // initialize socket
    if (!window.WebSocket) {
        alert("WebSockets are not supported by this browser");
    } else {
        $scope.initializeSocket();
        $scope.initializeSocket4Coupon();
    }        
});