/* route.js */

var joinEventHandler = require("./Handlers/joinEventHandler.js"),
	url = require("url");

exports.route = (function() {
	var handlers = {};
	
	handlers['/join'] = {
		POST : joinEventHandler.create
	};
	
	function route(req, res, body) {
		var pathname = url.parse(req.url).pathname;
		var method = req.method.toUpperCase();
		
		if(typeof handlers[pathname][method] === "function") {
			handlers[pathname][method](req, res, body);
		} else {
			res.writeHead(404, { "content-type" : "text/plain" });
			res.write("pathname error");
			res.end();
		}
	}
	
	return route;
})();
