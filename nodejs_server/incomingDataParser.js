/* incomingDataParser.js */

var	url        = require("url"),
	dataFilter = require("./inappropriateAccessFilter");
	

exports.dataParser = (function() {
	function dataParser(req, res, form) {
		form.parse(req, function(err, incomingContents, file) {
			if (err) console.log("data parsing error");
			
			if (file.image)
				incomingContents.imageUrl = file.image.path;
			
			var method = req.method.toUpperCase();
			var pathname = url.parse(req.url).pathname;
			
			if (method === "GET") {
				var path = [];
				path = pathname.split("/");
				pathname = "/" + path[1];
				incomingContents.imageName = path[2];
			}
			
			dataFilter.dataFilter(res, pathname, method, incomingContents);
		});

	}
	
	return dataParser;
})();
