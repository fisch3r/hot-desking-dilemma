'use strict';

// Declare app level module which depends on views, and components
var hotdesk = angular.module('hotdesk', [ 'ngRoute', 'hotdesk.view1',
		'hotdesk.view2', 'hotdesk.version', 'ui.bootstrap' ]);

hotdesk.run(function($rootScope, StateService) {
	var mqttClient = new Paho.MQTT.Client('192.168.1.10', Number(9001),
			"clientId");

	mqttClient.startTrace();
	mqttClient.onConnectionLost = onConnectionLost;
	mqttClient.onMessageArrived = onMessageArrived;
	mqttClient.connect({
		onSuccess : onConnect,
		userName : "user",
		password : "hot-desk"
	});

	// called when the client connects
	function onConnect() {
		// Once a connection has been made, make a subscription and send
		// a message.
		console.log("onConnect");
		mqttClient.subscribe("hot-desks/#");
	}

	// called when the client loses its connection
	function onConnectionLost(responseObject) {
		if (responseObject.errorCode !== 0) {
			console.log("onConnectionLost:" + responseObject.errorMessage);
		}
	}

	// called when a message arrives
	function onMessageArrived(message) {
		console.log("onMessageArrived:" + message.payloadString);

		var payload = JSON.parse(message.payloadString);
		StateService.state[payload.name] = payload;
		
		$rootScope.$broadcast('tableUpdate');
	}

	$rootScope.mqttClient = mqttClient;
});

hotdesk.factory('StateService', function() {
	return { state : {} };
});

hotdesk.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.otherwise({
		redirectTo : '/view1'
	});
} ]);
