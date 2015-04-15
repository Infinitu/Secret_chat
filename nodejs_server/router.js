/* route.js */

var	msgHandler = require("./handlers/msgHandler"),
	joinEventHandler = require("./handlers/joinEventHandler"),
	settingEventHandler = require("./handlers/settingEventHandler");

exports.route = (function() {
	var handlers = {};
	
	handlers["/join"] = joinEventHandler.create;
	handlers["/settingread"] = settingEventHandler.read;	
	handlers["/settingupdate"] = settingEventHandler.update;
	
	function route(req, res, pathname, contents) {
		if(typeof handlers[pathname] === "function")
			handlers[pathname](req, res, contents);
		
		else
			msgHandler.sendError(res, "router error");
	}
	
	return route;
})();
