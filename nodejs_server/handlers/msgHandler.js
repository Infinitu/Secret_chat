/* msgHandler.js */

var url = require("url");

exports.sendError = function(res, errorMsg) {
	res.writeHead(404, { "content-type" : "text/plain" });
	res.write(errorMsg);
	res.end();
};

exports.sendJoinConfirmed = function(res, accessToken) {
	res.writeHead(200, { "Content-type" : "text/plain" });
	res.write("good" + "|" + accessToken);
	res.end();
};

exports.sendJSON = function(res, msg) {
	console.log("msg :", msg);
	console.log("JSON msg :", JSON.stringify(msg));
	
	res.writeHead(200, { "Content-type" : "application/json" });
	res.write(JSON.stringify(msg));
	res.end();
};

