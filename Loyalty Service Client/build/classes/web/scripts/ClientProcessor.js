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
            var pointsRes = $resource($scope.restUri + ":rewards_id/points/unused", null, 
            {
                unusedPts: {
                    method: 'GET',
                    isArary: false
                }
            });
            
            // gets points, async call - callback.
            pointsRes.unusedPts({rewards_id: $scope.cust_id}, 
                
                // callback
                function(points) {
                    delete points.$promise;
                    delete points.$resolved;

                    //console.log(points);

                    var str = '';
                    for (var p in points) {
                        if (points.hasOwnProperty(p)) {
                            str += points[p];
                        }
                    }
                    $scope.avail_points = str;
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

    $scope.sendEmail = function() 
    {
        input_email = document.getElementById("input_email").value;

        if(input_email === "") {
            console.log("no email provided");
        } else {
            ws.send(input_email);
        }
    };
    
    $scope.sendNewCoupon = function()
    {
        var pointsRes = $resource($scope.restUri + ":rewards_id/points/unused", null, 
        {
            createCoupon: {
                method: 'POST',
                isArary: false
            }
        });

        pointsRes.createCoupon({rewards_id: $scope.cust_id}, 

            // callback
            function(points) {
                delete points.$promise;
                delete points.$resolved;

                console.log(points);

                var str = '';
                for (var p in points) {
                    if (points.hasOwnProperty(p)) {
                        str += points[p];
                    }
                }

                $scope.avail_points = str;
            }
        );
    };
    
    $scope.calcEquivDollar = function()
    {
        if($scope.pts_to_use);
    };
    
    // initialize socket
    if (!window.WebSocket) {
        alert("WebSockets are not supported by this browser");
    } else {
        $scope.initializeSocket();
    }        
});