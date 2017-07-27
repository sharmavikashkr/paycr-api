var app = angular.module('payCrAdminApp', [ "ngRoute", "ngCookies", "ngMaterial" ]);
app.config(function($mdDateLocaleProvider) {
    $mdDateLocaleProvider.formatDate = function(date) {
       return moment(date).format('YYYY-MM-DD');
    };
});
app.controller('AdminController',
function($scope, $http, $cookies, $httpParamSerializer, $timeout) {
	var dateNow = moment().toDate();
	var dateStart = moment().subtract(30, 'day').toDate();
	$scope.server = {
		"hideMessage" : true,
		"respStatus" : "WELCOME!",
		"respMsg" : ":)",
		"isSuccess" : true
	}
	$scope.patterns = {
		"paramNamePattern" : "\\w{1,10}",
		"namePattern" : "[0-9a-zA-Z_\\- ]{1,50}",
		"emailPattern" : "([a-zA-Z0-9_.]{1,})((@[a-zA-Z]{2,})[\\\.]([a-zA-Z]{2}|[a-zA-Z]{3}))",
		"mobilePattern" : "\\d{10}",
		"amountPattern" : "\\d{1,7}",
		"numberPattern" : "\\d{1,4}"
	}
	$scope.searchMerchantReq = {
		"name" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$scope.searchInvoiceReq = {
		"invoiceCode" : "",
		"email" : "",
		"mobile" : "",
		"createdFrom" : dateStart,
		"createdTo" : dateNow
	}
	$scope.dismissServerAlert = function() {
		$scope.server.hideMessage = true;
	}
	$scope.serverMessage = function(data) {
		$scope.server.hideMessage = false;
		if(data.status==200) {
			$scope.server.isSuccess = true;
			$scope.server.respStatus = "SUCCESS!";
			$scope.server.respMsg = "operation successful";
		} else if(data.status==401) {
			$scope.server.isSuccess = false;
			$scope.server.respStatus = "FAILURE!";
			$scope.server.respMsg = "unauthorized request";
			$scope.logout();
		} else {
			$scope.server.isSuccess = false;
			$scope.server.respStatus = "FAILURE!";
			$scope.server.respMsg = data.headers('error_message');
		}
	}
	$scope.prepare = function() {
		$scope.fetchUser();
		$scope.fetchNotifications();
		$scope.fetchEnums();
	}
	$scope.fetchPriceSettings = function() {
		$scope.fetchPricings();
		$scope.fetchSubsModes();
	}
	$scope.fetchEnums = function() {
		var req = {
			method : 'GET',
			url : "/enum/paymodes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(payModes) {
			$scope.payModes = payModes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/usertypes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(usertypes) {
			$scope.userTypes = usertypes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/timeranges",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(timeranges) {
			$scope.timeRanges = timeranges.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/invoicestatuses",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(invoicestatuses) {
			$scope.invoiceStatuses = invoicestatuses.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
		
		var req = {
			method : 'GET',
			url : "/enum/paytypes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(paytypes) {
			$scope.payTypes = paytypes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchUser = function() {
		var req = {
			method : 'GET',
			url : "/common/user",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(user) {
			$scope.user = user.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchNotifications = function() {
		var req = {
			method : 'GET',
			url : "/common/notifications",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(notifications) {
			$scope.notices = notifications.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchMyInvoices = function() {
		var req = {
			method : 'GET',
			url : "/common/invoices/",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(myInvoices) {
			$scope.myinvoices = myInvoices.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.updateInvoiceInfo = function(invoice) {
		$scope.invoiceInfo = invoice;
	}
	$scope.updateOffSubsMerchant = function(merchant) {
		$scope.offSubsmerchant = merchant;
	}
	$scope.searchMerchant = function() {
		var req = {
			method : 'POST',
			url : "/search/merchant",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.searchMerchantReq
		}
		$http(req).then(function(merchants) {
			$scope.merchantList = merchants.data;
			$scope.loadMerchantPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.searchInvoice = function() {
		var req = {
			method : 'POST',
			url : "/search/invoice",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : $scope.searchInvoiceReq
		}
		$http(req).then(function(invoices) {
			$scope.invoiceList = invoices.data;
			$scope.loadInvoicePage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchMyUsers = function() {
		var req = {
			method : 'GET',
			url : "/common/users",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(myusers) {
			$scope.myusers = myusers.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createUser = function() {
		if(!this.addUserForm.$valid) {
			return false;
		}
		var req = {
			method : 'post',
			url : "/common/create/user",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newuser
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchMyUsers();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createUserModal')).modal('hide');
	}
	$scope.toggleUser = function(userId) {
		var req = {
			method : 'get',
			url : "/common/toggle/user/" + userId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.serverMessage(data);
			$scope.fetchMyUsers();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchPricings = function() {
		var req = {
			method : 'GET',
			url : "/common/pricings",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(pricings) {
			$scope.pricings = pricings.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createPricing = function() {
		if(!this.createPricingForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/pricing/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newpricing
		}
		$http(req).then(function(data) {
			$scope.fetchPricings();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createPricingModal')).modal('hide');
	}
	$scope.togglePricing = function(pricingId) {
		var req = {
			method : 'GET',
			url : "/admin/pricing/toggle/" + pricingId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.fetchPricings();
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.fetchSubsModes = function() {
		var req = {
			method : 'GET',
			url : "/subscription/modes",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(subsModes) {
			$scope.subsModes = subsModes.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createSubsMode = function() {
		if(!this.createSubsModeForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/subscription/mode/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newsubsmode
		}
		$http(req).then(function(data) {
			$scope.fetchSubsModes();
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#addSubsModeModal')).modal('hide');
	}
	$scope.toggleSubsMode = function(modeId) {
		var req = {
			method : 'GET',
			url : "/subscription/mode/toggle/" + modeId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(data) {
			$scope.fetchSubsModes();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createOfflineSubs = function(merchantId) {
		this.offlinesubs.merchantId = merchantId;
		var req = {
			method : 'POST',
			url : "/subscription/new/offline",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.offlinesubs
		}
		$http(req).then(function(data) {
			$scope.searchMerchant(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createOfflineSubsModal')).modal('hide');
	}
	$scope.fetchSubscriptionDetails = function(subscriptionId) {
		var req = {
			method : 'GET',
			url : "/subscription/get/" + subscriptionId,
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(subscription) {
			$scope.subsinfo = subscription.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createMerchant = function() {
		if(!this.createMerchantForm.$valid) {
			return false;
		}
		var req = {
			method : 'POST',
			url : "/admin/merchant/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : this.newmerchant
		}
		$http(req).then(function(data) {
			$scope.searchMerchant(1);
			$scope.serverMessage(data);
		}, function(data) {
			$scope.serverMessage(data);
		});
		angular.element(document.querySelector('#createMerchantModal')).modal('hide');
	}
	$scope.fetchReports = function() {
		var req = {
			method : 'GET',
			url : "/reports",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			}
		}
		$http(req).then(function(reports) {
			$scope.reports = reports.data;
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.createReport = function() {
		var req = {
			method : 'POST',
			url : "/reports/new",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
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
	}
	$scope.loadReport = function(report) {
		var req = {
			method : 'POST',
			url : "/reports/load",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token")
			},
			data : report
		}
		$http(req).then(function(invoiceReports) {
			$scope.invoiceReports = invoiceReports.data;
			$scope.loadedreport = angular.copy(report);
			$scope.loadReportPage(1);
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.downloadReport = function(report) {
		var req = {
			method : 'POST',
			url : "/reports/download",
			headers : {
				"Authorization" : "Bearer "
						+ $cookies.get("access_token"),
				"Accept" : "text/csv"
			},
			data : report
		}
		$http(req).then(function(content) {
		    var hiddenElement = document.createElement('a');
		    hiddenElement.href = 'data:attachment/csv,' + encodeURI(content.data);
		    hiddenElement.target = '_blank';
		    hiddenElement.download = 'report.csv';
		    hiddenElement.click();
		}, function(data) {
			$scope.serverMessage(data);
		});
	}
	$scope.loadInvoicePage = function(page) {
		var pageSize = 15;
		$scope.invoiceResp = {};
		$scope.invoiceResp.invoiceList = angular.copy($scope.invoiceList);
		$scope.invoiceResp.invoiceList.splice(pageSize * page, $scope.invoiceList.length - pageSize);
		$scope.invoiceResp.invoiceList.splice(0, pageSize * (page - 1));
		$scope.invoiceResp.page = page;
		$scope.invoiceResp.allPages = [];
		var noOfPages = $scope.invoiceList.length/pageSize;
		if($scope.invoiceList.length%pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for(var i = 1; i <= noOfPages; i++) {
			$scope.invoiceResp.allPages.push(i);
		}
	}
	$scope.loadMerchantPage = function(page) {
		var pageSize = 15;
		$scope.merchantResp = {};
		$scope.merchantResp.merchantList = angular.copy($scope.merchantList);
		$scope.merchantResp.merchantList.splice(pageSize * page, $scope.merchantList.length - pageSize);
		$scope.merchantResp.merchantList.splice(0, pageSize * (page - 1));
		$scope.merchantResp.page = page;
		$scope.merchantResp.allPages = [];
		var noOfPages = $scope.merchantList.length/pageSize;
		if($scope.merchantList.length%pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for(var i = 1; i <= noOfPages; i++) {
			$scope.merchantResp.allPages.push(i);
		}
	}
	$scope.loadReportPage = function(page) {
		var pageSize = 15;
		$scope.reportsResp = {};
		$scope.reportsResp.invoiceReports = angular.copy($scope.invoiceReports);
		$scope.reportsResp.invoiceReports.splice(pageSize * page, $scope.invoiceReports.length - pageSize);
		$scope.reportsResp.invoiceReports.splice(0, pageSize * (page - 1));
		$scope.reportsResp.page = page;
		$scope.reportsResp.allPages = [];
		var noOfPages = $scope.invoiceReports.length/pageSize;
		if($scope.invoiceReports.length%pageSize != 0) {
			noOfPages = noOfPages + 1;
		}
		for(var i = 1; i <= noOfPages; i++) {
			$scope.reportsResp.allPages.push(i);
		}
	}
	$scope.logout = function() {
		$timeout(function(){
			window.location.href="/adminlogin?logout";
		}, 1000); 
	}
});