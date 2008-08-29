(function() { // Create a local scope

rptools.test = {
	accessJavaPackage: function() {
		return new java.util.Date();
	},
	
	pi: function() {
		return java.lang.Math.PI;
	},
	
	_counter: 0,
	
	increment: function() {
		return this._counter++;
	},
	
	echo: function(a) {
		return a;
	},
	
	add: function(a) {
		return a + 1;
	},
	
	arrayLength: function(a) {
		return a.length;
	},
	
	parserFunction: function() {
		var total = 0;
		
		for (var i = 1; i < arguments.length; i++)
			total += arguments[i];
		
		return arguments[0] + ": " + total;
	},
	
	parserFunctionFixedArguments: function(a, b) {
		return a + b;
	}
};

})(); // End local scope
