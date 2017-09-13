app.controller('DashboardController', function($scope, $rootScope, $http,
		$cookies) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.statsReq = {
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$scope.loadDashboard = function() {
		angular.element(document.querySelector('#spinnerModal')).modal('show');
		var req = {
			method : 'POST',
			url : "/common/dashboard",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : $scope.statsReq
		}
		$http(req).then(function(response) {
			$rootScope.statsResponse = response.data;
			if($rootScope.statsResponse.dailyPayList.length > 0) {
				$scope.loadCharts();				
			}
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	
	$scope.loadCharts = function() {
		$('#amount-donut').html('');
		$('#count-donut').html('');
		$('#per-day-area').html('');
		Morris.Donut({
	        element: 'amount-donut',
	        colors : ['#3c763d', '#31708f', '#333', '#faf2cc', '#a94442'],
	        data: [{
	            label: "Paid",
	            value: $rootScope.statsResponse.salePaySum
	        }, {
	            label: "Refunded",
	            value: $rootScope.statsResponse.refundPaySum
	        }, {
	            label: "Expired",
	            value: $rootScope.statsResponse.expiredInvSum
	        }, {
	            label: "Unpaid",
	            value: $rootScope.statsResponse.unpaidInvSum
	        }, {
	            label: "Declined",
	            value: $rootScope.statsResponse.declinedInvSum
	        }],
	        resize: true
	    });
		Morris.Donut({
	        element: 'count-donut',
	        colors : ['#3c763d', '#31708f', '#333', '#faf2cc', '#a94442'],
	        data: [{
	            label: "Paid",
	            value: $rootScope.statsResponse.salePayCount
	        }, {
	            label: "Refunded",
	            value: $rootScope.statsResponse.refundPayCount
	        }, {
	            label: "Expired",
	            value: $rootScope.statsResponse.expiredInvCount
	        }, {
	            label: "Unpaid",
	            value: $rootScope.statsResponse.unpaidInvCount
	        }, {
	            label: "Declined",
	            value: $rootScope.statsResponse.declinedInvCount
	        }],
	        resize: true
	    });
		var areaData = [];
		for(var dailyPay in $rootScope.statsResponse.dailyPayList) {
			areaData.push({
				period: $rootScope.statsResponse.dailyPayList[dailyPay].created,
				paid: $rootScope.statsResponse.dailyPayList[dailyPay].salePaySum,
				refund: $rootScope.statsResponse.dailyPayList[dailyPay].refundPaySum
			});
		}
		Morris.Bar({
	        element: 'per-day-bar',
	        data: areaData,
	        xkey: 'period',
	        ykeys: ['paid', 'refund'],
	        labels: ['paid', 'refund'],
	        hideHover: 'auto',
	        resize: true
	    });
		angular.element(document.querySelector('#spinnerModal')).modal('hide');
	}
});