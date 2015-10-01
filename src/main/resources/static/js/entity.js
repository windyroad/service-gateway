var app = angular.module('serviceGateway', []);

app.controller('EntityController', function($scope, $http) {
	var controller = this;

	$http.get('/admin/proxies').success(function(data) {
		controller.entity = data;
	})

	controller.processForm = function() {
		console.log("processForm");
		var action = controller.entity.actions[0];

		$http(
				{
					method : action.method || "GET",
					url : action.href,
					data : $.param(action.fields), // pass in data as strings
					headers : {
						'Content-Type' : action.type
								|| "application/x-www-form-urlencoded",
						'Accept' : "application/vnd.siren+json"
					}
				// set the headers so angular passing info as form data (not
				// request
				// payload)
				}).then(function successCallback(response) {
			if (response.status == 201) {
				var location = response.headers("Location");
				console.log("LOC: " + location);
				$http.get(location).then(function successCallback(response) {
					controller.entity = response.data;
				}, 
						function errorCallback(response) {
					alert("TODO: location follow error handing");
					});
			}
		}, function errorCallback(response) {
			alert("TODO: error handing");
		});
	};

	controller.todos = [ {
		text : 'learn angular',
		done : true
	}, {
		text : 'build an angular app',
		done : false
	} ];

	controller.addTodo = function() {
		controller.todos.push({
			text : controller.todoText,
			done : false
		});
		controller.todoText = '';
	};

	controller.remaining = function() {
		var count = 0;
		angular.forEach(controller.todos, function(todo) {
			count += todo.done ? 0 : 1;
		});
		return count;
	};

	controller.archive = function() {
		var oldTodos = controller.todos;
		controller.todos = [];
		angular.forEach(oldTodos, function(todo) {
			if (!todo.done)
				controller.todos.push(todo);
		});
	};
});