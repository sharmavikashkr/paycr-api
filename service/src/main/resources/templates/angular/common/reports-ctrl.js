app.controller('ReportsController', function($scope, $rootScope, $http,
		$cookies) {
	$rootScope.fetchReports = function() {
		var req = {
			method : 'GET',
			url : "/reports",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(reports) {
			$rootScope.reports = reports.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createReport = function() {
		var req = {
			method : 'POST',
			url : "/reports/new",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : this.newreport
		}
		$http(req).then(function(data) {
			$scope.fetchReports();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		this.newreport = null;
		angular.element(document.querySelector('#createReportModal')).modal('hide');
	}
	$scope.loadReport = function(report) {
		var req = {
			method : 'POST',
			url : "/reports/load",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : report
		}
		$http(req).then(function(reportData) {
			$rootScope.reportData = reportData.data;
			$rootScope.loadedreport = angular.copy(report);
			$rootScope.reportsResp = {};
			$scope.loadReportPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createReportModal')).modal('hide');
	}
	$scope.loadReportPage = function(page) {
		var pageSize = 15;
		$rootScope.reportsResp = {};
		if($rootScope.loadedreport.reportType == 'INVOICE') {
			$rootScope.reportsResp.invoiceReports = angular.copy($rootScope.reportData);
			$rootScope.reportsResp.invoiceReports.splice(pageSize * page, $rootScope.reportData.length - pageSize);
			$rootScope.reportsResp.invoiceReports.splice(0, pageSize * (page - 1));
		} else if($rootScope.loadedreport.reportType == 'EXPENSE') {
			$rootScope.reportsResp.expenseReports = angular.copy($rootScope.reportData);
			$rootScope.reportsResp.expenseReports.splice(pageSize * page, $rootScope.reportData.length - pageSize);
			$rootScope.reportsResp.expenseReports.splice(0, pageSize * (page - 1));
		} else if($rootScope.loadedreport.reportType == 'CONSUMER') {
			$rootScope.reportsResp.consumerReports = angular.copy($rootScope.reportData);
			$rootScope.reportsResp.consumerReports.splice(pageSize * page, $rootScope.reportData.length - pageSize);
			$rootScope.reportsResp.consumerReports.splice(0, pageSize * (page - 1));
		} else if($rootScope.loadedreport.reportType == 'SUPPLIER') {
			$rootScope.reportsResp.supplierReports = angular.copy($rootScope.reportData);
			$rootScope.reportsResp.supplierReports.splice(pageSize * page, $rootScope.reportData.length - pageSize);
			$rootScope.reportsResp.supplierReports.splice(0, pageSize * (page - 1));
		}
		$rootScope.reportsResp.page = page;
		$rootScope.reportsResp.allPages = [];
		var noOfPages = $rootScope.reportData.length / pageSize;
		if ($rootScope.reportData.length % pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for (var i = 1; i <= noOfPages; i++) {
			$rootScope.reportsResp.allPages.push(i);
		}
	}
	$scope.downloadReport = function(report) {
		var req = {
			method : 'POST',
			url : "/reports/download",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token"),
				"Accept" : "text/csv"
			},
			data : report
		}
		$http(req).then(function(content) {
			var hiddenElement = document.createElement('a');
			hiddenElement.href = 'data:attachment/csv,'
					+ encodeURI(content.data);
			hiddenElement.target = '_blank';
			hiddenElement.download = 'report.csv';
			hiddenElement.click();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.mailReport = function(report) {
		var req = {
			method : 'POST',
			url : "/reports/mail",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			},
			data : report
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.deleteReport = function(reportId, reportName) {
		if (!confirm('Delete ' + reportName + ' ?')) {
			return false;
		}
		var req = {
			method : 'GET',
			url : "/reports/delete/" + reportId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchReports();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.getSchedule = function() {
		var req = {
			method : 'GET',
			url : "/reports/schedule/get",
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(recRepUsers) {
			$rootScope.recRepUsers = recRepUsers.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.addSchedule = function(reportId) {
		var req = {
			method : 'GET',
			url : "/reports/schedule/add/" + reportId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.getSchedule();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#recurrReportModal')).modal('hide');
	}
	$scope.removeSchedule = function(recRepUserId) {
		var req = {
			method : 'GET',
			url : "/reports/schedule/remove/" + recRepUserId,
			headers : {
				"Authorization" : "Bearer " + $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.getSchedule();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
});