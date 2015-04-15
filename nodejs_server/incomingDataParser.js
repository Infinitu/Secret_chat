/* incomingDataParser.js */

var url = require("url"),	
	formidable = require("formidable"),
	router = require("./router")
	msgHandler = require("./handlers/msgHandler");

exports.dataParse = (function() {
	var contents = {};

	function dataParse(req, res, form) {
		form.parse(req, function(err, contents) {
			if(err)
				msgHandler.sendError(res, "data parsing error");
			
			var pathname = url.parse(req.url).pathname;
			router.route(req, res, pathname, contents);
		});
	}
	
	return dataParse;
})();
