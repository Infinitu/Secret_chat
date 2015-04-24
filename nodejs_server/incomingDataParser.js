/* incomingDataParser.js */

var	url        = require("url"),	
	formidable = require("formidable"),
	router     = require("./router"),
	msgHandler = require("./handlers/msgHandler");

form.uploadDir = "./profileImages";
form.keepExtensions = true;
form.maxFieldsSize  = 10 * 1024 * 1024;  // 최대 보낼 수 있는 파일 용량 10 mb

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
