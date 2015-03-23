'use strict';

hotdesk.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/overview', {
		templateUrl : 'overview/overview.html',
		controller : 'OverviewCtrl'
	});
} ])

hotdesk.controller('OverviewCtrl', ['$scope', 'StateService', function($scope, StateService) {
	$scope.tables = StateService.state;

	$scope.$on('tableUpdate', function(event) {
		$scope.tables = StateService.state;
		$scope.latestUpdate = new Date();
		$scope.$apply();
	});
}]);