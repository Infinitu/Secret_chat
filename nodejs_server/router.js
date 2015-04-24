/* route.js */

var	msgHandler         = require("./handlers/msgHandler"),
	userEventHandler   = require("./handlers/userEventHandler"),
	friendEventHandler = require("./handlers/friendEventHandler");

exports.route = (function() {
	var handlers = { "/join"      : { POST : userEventHandler.join },
					 "/uninstall" : { DELETE : userEventHandler.remove },
					 "/setting"   : { POST : userEventHandler.read,   PUT : userEventHandler.update },
					 "/addfriend" : { POST : friendEventHandler.find },
					 "/main"      : { POST : friendEventHandler.read }
	};
	
	function route(res, pathname, method, contents) {
		if (typeof handlers[pathname][method] === "function")
			handlers[pathname][method](res, contents);
		
		else
			msgHandler.sendError(res, "router error");
	}
	
	return route;
})();
