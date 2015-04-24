/* route.js */

var userEventHandler   = require("./handlers/userEventHandler"),
	friendEventHandler = require("./handlers/friendEventHandler");

exports.route = (function() {
	var handlers = { "/join"      : { POST : userEventHandler.join },
					 "/setting"   : { POST : userEventHandler.read, PUT : userEventHandler.update },
					 "/addfriend" : { POST : friendEventHandler.find },
					 "/main"      : { POST : friendEventHandler.read },
					 "/uninstall" : { DELETE : userEventHandler.remove }
	};
	
	function route(res, pathname, method, contents) {
		if (typeof handlers[pathname][method] === "function")
			handlers[pathname][method](res, contents);
		
		else
			console.log("router error");
	}
	
	return route;
})();
