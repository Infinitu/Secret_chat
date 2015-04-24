/* incomingDataParser.js */

var	url        = require("url"),	
	formidable = require("formidable"),
	router     = require("./router"),
	msgHandler = require("./handlers/msgHandler");

exports.dataParse = (function() {
	function dataParse(req, res, form) {   // ** form의 크기 정하기 (전송 가능한 용량을 확인하여 그 이상은 block)
		var UPLOAD_FOLDER = "./profileImages";
		
		form.uploadDir = UPLOAD_FOLDER;
		form.keepExtensions = true;
		
		form.parse(req, function(err, inputContents, file) {
			if (err)
				msgHandler.sendError(res, "data parsing error");
			
			if(file.image)
				inputContents.imageUrl = file.image.path;
			
			var pathname = url.parse(req.url).pathname;
			var method = req.method.toUpperCase();
			
			router.route(res, pathname, method, inputContents);
		});
	}
	
	return dataParse;
})();
