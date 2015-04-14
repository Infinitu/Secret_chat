/* route.js */

var	url = require("url"),
	msgUtil = require("./handlers/msgHandler.js"),
	joinEventHandler = require("./handlers/joinEventHandler.js"),
	settingEventHandler = require("./handlers/settingEventHandler.js");

exports.route = (function() {
	var handlers = {};
	
	handlers["/join"] = {
		POST : joinEventHandler.create
	};
	
	handlers["/setting"] = {
		POST : settingEventHandler.read,
		PUT : settingEventHandler.update		
	};
	
	function route(req, res, pathname, method, contents) {
		if(typeof handlers[pathname][method] === "function")
			handlers[pathname][method](req, res, contents);
		
		else
			msgUtil.sendError(res, "router error");
	}
	
	return route;
})();
