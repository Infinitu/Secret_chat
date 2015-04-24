/* incomingDataParser.js */

var	url        = require("url"),	
	router     = require("./router"),
	msgHandler = require("./handlers/msgHandler");

exports.dataParse = (function() {
	function dataParse(req, res, form) {
		form.parse(req, function(err, inputContents, file) {
			if (err) msgHandler.sendError(res, "data parsing error");
			
			if (file.image)
				inputContents.imageUrl = file.image.path;
			
			var pathname = url.parse(req.url).pathname;
			var method = req.method.toUpperCase();
			
			router.route(res, pathname, method, inputContents);
		});
	}
	
	return dataParse;
})();
