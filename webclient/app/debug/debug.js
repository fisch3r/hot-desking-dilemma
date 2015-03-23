'use strict';

hotdesk.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/debug', {
    templateUrl: 'debug/debug.html',
    controller: 'DebugCtrl'
  });
}]);

hotdesk.controller('DebugCtrl', ['$scope', '$rootScope', 'MqttService', function($scope, $rootScope, MqttService) {
	$scope.message = {
		name: 'table1',
		description: '',
		status: 'free'
	};

	$scope.send = function() {
		console.log("send");
		MqttService.sendMessage($scope.message);
	}
}]);