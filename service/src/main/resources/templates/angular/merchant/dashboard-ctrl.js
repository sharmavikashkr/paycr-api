app.controller('DashboardController', function($scope, $rootScope, $http,
		$cookies) {
	$scope.timeRange = 'LAST_WEEK';
	$rootScope.loadDashboard = function() {
		var req = {
			method : 'GET',
			url : "/merchant/dashboard/" + $scope.timeRange,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(response) {
			$rootScope.statsResponse = response.data;
			if($rootScope.statsResponse.dailyInvPayList.length > 0) {
				$scope.loadInvCharts();
			}
			if($rootScope.statsResponse.dailyExpPayList.length > 0) {
				$scope.loadExpCharts();
			}
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	
	$scope.loadInvCharts = function() {
		$('#inv-amount-donut').empty();
		$('#inv-count-donut').empty();
		$('#inv-per-day-bar').empty();
		
		Morris.Donut({
	        element: 'inv-amount-donut',
	        colors : ['#00a65a', '#dd4b39', '#d2d6de', '#faf2cc', '#f39c12'],
	        data: [{
	            label: "Paid",
	            value: $rootScope.statsResponse.saleInvPaySum
	        }, {
	            label: "Refunded",
	            value: $rootScope.statsResponse.refundInvPaySum
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
	        element: 'inv-count-donut',
	        colors : ['#00a65a', '#dd4b39', '#d2d6de', '#faf2cc', '#f39c12'],
	        data: [{
	            label: "Paid",
	            value: $rootScope.statsResponse.saleInvPayCount
	        }, {
	            label: "Refunded",
	            value: $rootScope.statsResponse.refundInvPayCount
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
		var areaInvData = [];
		for(var dailyInvPay in $rootScope.statsResponse.dailyInvPayList) {
			areaInvData.push({
				period: $rootScope.statsResponse.dailyInvPayList[dailyInvPay].created,
				paid: $rootScope.statsResponse.dailyInvPayList[dailyInvPay].salePaySum,
				refund: $rootScope.statsResponse.dailyInvPayList[dailyInvPay].refundPaySum
			});
		}
		Morris.Bar({
	        element: 'inv-per-day-bar',
	        data: areaInvData,
	        xkey: 'period',
	        ykeys: ['paid', 'refund'],
	        labels: ['paid', 'refund'],
	        hideHover: 'auto',
	        resize: true
	    });
		
		$rootScope.hideSpinner = true;
	}
	
	$scope.loadExpCharts = function() {
		$('#exp-amount-donut').empty();
		$('#exp-count-donut').empty();
		$('#exp-per-day-bar').empty();
		Morris.Donut({
	        element: 'exp-amount-donut',
	        colors : ['#00a65a', '#dd4b39', '#faf2cc'],
	        data: [{
	            label: "Paid",
	            value: $rootScope.statsResponse.saleExpPaySum
	        }, {
	            label: "Refunded",
	            value: $rootScope.statsResponse.refundExpPaySum
	        }, {
	            label: "Unpaid",
	            value: $rootScope.statsResponse.unpaidExpSum
	        }],
	        resize: true
	    });
		Morris.Donut({
	        element: 'exp-count-donut',
	        colors : ['#00a65a', '#dd4b39', '#faf2cc'],
	        data: [{
	            label: "Paid",
	            value: $rootScope.statsResponse.saleExpPayCount
	        }, {
	            label: "Refunded",
	            value: $rootScope.statsResponse.refundExpPayCount
	        }, {
	            label: "Unpaid",
	            value: $rootScope.statsResponse.unpaidExpCount
	        }],
	        resize: true
	    });
		var areaExpData = [];
		for(var dailyExpPay in $rootScope.statsResponse.dailyExpPayList) {
			areaExpData.push({
				period: $rootScope.statsResponse.dailyExpPayList[dailyExpPay].created,
				paid: $rootScope.statsResponse.dailyExpPayList[dailyExpPay].salePaySum,
				refund: $rootScope.statsResponse.dailyExpPayList[dailyExpPay].refundPaySum
			});
		}
		Morris.Bar({
	        element: 'exp-per-day-bar',
	        data: areaExpData,
	        xkey: 'period',
	        ykeys: ['paid', 'refund'],
	        labels: ['paid', 'refund'],
	        hideHover: 'auto',
	        resize: true
	    });
		
		$rootScope.hideSpinner = true;
	}
});