/* incomingDataParser.js */

var	url    = require("url"),	
	router = require("./router"),
	cipherHandler = require("./handlers/cipherHandler");

exports.dataParse = (function() {
	function dataParse(req, res, form) {
		form.parse(req, function(err, inputContents, file) {
			if (err) console.log("data parsing error");
			
			if (inputContents.accessToken) {
				cipherHandler.decryptToken(inputContents.accessToken, function(token) {
					inputContents.accessToken = token;
				});
			}
			
			if (file.image)
				inputContents.imageUrl = file.image.path;
			
			var method = req.method.toUpperCase();
			var pathname = url.parse(req.url).pathname;
			
			if (method === "GET") {
				var path = [];
				path = pathname.split("/");
				pathname = "/" + path[1];
				inputContents.imageName = path[2];
			}
			
			router.route(res, pathname, method, inputContents);
		});

	}
	
	return dataParse;
})();
