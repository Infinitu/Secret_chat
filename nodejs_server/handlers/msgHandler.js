/* msgHandler.js */

var mime = require("mime");

exports.sendString = function(res, msg) {
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

exports.sendFile = function(res, filePath, file) {
	console.log("send file");
	console.log("file : ", file);
	res.writeHead(200, { "Content-type" : mime.lookup(file.Path) });
	res.write(file);
	res.end();
};

exports.sendError = function(res, errorMsg) {
	res.writeHead(404, { "content-type" : "text/plain" });
	res.write(errorMsg);
	res.end();
};
