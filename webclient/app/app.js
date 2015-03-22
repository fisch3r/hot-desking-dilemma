'use strict';

// Declare app level module which depends on views, and components
var hotdesk = angular.module('hotdesk', [ 'ngRoute', 'hotdesk.view1',
		'hotdesk.view2', 'hotdesk.version', 'ui.bootstrap' ]);

hotdesk.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.otherwise({
		redirectTo : '/view1'
	});
} ]);
