/* fieldParser.js */

var	formidable = require("formidable"),
	url = require("url"),
	router = require("./router.js")
	msgUtil = require("./handlers/msgHandler.js");

exports.fieldParse = (function() {
	var contents = {};

	function fieldParse(req, res, form) {
		form.on('field', function(key, value) {
			contents[key] = value;
		    });
		form.parse(req, function(err, fields) {
			if(err)
				msgUtil.sendError(res, "field parse error");
			
			var pathname = url.parse(req.url).pathname;
			var method = req.method.toUpperCase();
			
			router.route(req, res, pathname, method, contents);
		});
	}
	
	return fieldParse;
})();
