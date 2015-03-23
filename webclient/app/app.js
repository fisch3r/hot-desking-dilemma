'use strict';

// Declare app level module which depends on views, and components
var hotdesk = angular.module('hotdesk', [ 'ngRoute', 'ui.bootstrap' ]);

hotdesk.run(['$rootScope', 'MqttService', function($rootScope, MqttService) {
	console.log("run: " + MqttService);
	MqttService.connect();
}]);

hotdesk.factory('MqttService', ['$rootScope', 'StateService', function($rootScope, StateService) {
	var mqttClient;
	
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

	return {
		connect: function() {
			mqttClient = new Paho.MQTT.Client('192.168.1.10', Number(9001), "hotdesk-webclient-" + Math.random());
			mqttClient.onConnectionLost = onConnectionLost;
			mqttClient.onMessageArrived = onMessageArrived;
			mqttClient.connect({
				onSuccess : onConnect,
				userName : "user",
				password : "hot-desk"
			});
		}, 
		sendMessage: function sendMessage(message) {
			var payload = JSON.stringify(message);
			var mqttMessage = new Paho.MQTT.Message(payload);
			mqttMessage.retained = true;
			mqttMessage.destinationName = "hot-desks/" + message.name;
			mqttClient.send(mqttMessage);
		}
    };
}]);

hotdesk.factory('StateService', function() {
	return { state : {} };
});

hotdesk.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.otherwise({
		redirectTo : '/overview'
	});
} ]);
