userManagerApp.controller('MainController', ['$scope', '$http',
function($scope, $http) {
$scope.users = null;

	var getUsers = function() {       
		$http.get("/UserManagementSystem/manage?request=getUsers")
			.success(function(data) {
				$scope.users = data;
			})
			.error(function(data) {
				console.log(data || "Request failed");
		});     
	};
  
	getUsers();
  
	$scope.addUser = function() {
		var data = {
        	 		"name": $scope.name,
        	 		"phone": $scope.phone,
        	 		"address": $scope.address,
        	 		"role": $scope.role
	  				};
	  
		$http.post("/UserManagementSystem/manage?request=addUser", data)
			.success(getUsers);  
	};
  
	$scope.removeUser = function(index) {
		var data = {
        	 		"name": $scope.users[index].name,
        	 		"phone": $scope.users[index].phone,
        	 		"address": $scope.users[index].address,
        	 		"role": $scope.users[index].role
	  				};
	  
		$http.post("/UserManagementSystem/manage?request=removeUser", data)
			.success(getUsers);  
	};
  
	$scope.updateUser = function(index) {
		var data = {
        	 		"name": $scope.users[index].name,
        	 		"phone": $scope.users[index].phone,
        	 		"address": $scope.users[index].address,
        	 		"role": $scope.users[index].role
	  				};
	  
		$http.post("/UserManagementSystem/manage?request=updateUser", data)
			.success(getUsers);  
  };
  
  
}]);