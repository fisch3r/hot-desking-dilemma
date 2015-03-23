'use strict';

angular.module('hotdesk.view1', [ 'ngRoute' ])

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/view1', {
		templateUrl : 'view1/view1.html',
		controller : 'View1Ctrl'
	});
} ])

.controller('View1Ctrl', function($scope, StateService) {
	$scope.tables = StateService.state;

	$scope.$on('tableUpdate', function(event) {
		$scope.tables = StateService.state;
		$scope.latestUpdate = new Date();
		$scope.$apply();
	});
});