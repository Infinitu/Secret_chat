/* msgHandler.js */

var mime = require("mime");

exports.sendString = function(res, msg) {
	console.log("send string");
	res.writeHead(200, { "Content-type" : "text/plain" });
	res.write(msg);
	res.end();
};

exports.sendJSON = function(res, JSONmsg) {
	console.log("send JSON");
	res.writeHead(200, { "Content-type" : "application/json" });
	res.write(JSON.stringify(JSONmsg));
	res.end();
};

exports.sendFile = function(res, file, filePath) {
	console.log("send file");
	res.writeHead(200, { "Content-type" : mime.lookup(filePath) });
	res.write(file);
	res.end();
};

exports.sendError = function(res) {	
	console.log("send error");
	res.writeHead(404, { "content-type" : "text/plain" });
	res.write("error");
	res.end();
};
