var app = angular.module('serviceGateway', []);

app.controller('EntityController', function($scope, $http) {
  var controller = this;
  
  $http.get('/admin/proxies').success(function(data) {
    controller.entity = data;
  })
  controller.todos = [
    {text:'learn angular', done:true},
      {text:'build an angular app', done:false}];
 
    controller.addTodo = function() {
      controller.todos.push({text:controller.todoText, done:false});
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
        if (!todo.done) controller.todos.push(todo);
      });
    };
});