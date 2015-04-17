/* msgHandler.js */

exports.sendString = function(res, msg) {
	res.writeHead(200, { "Content-type" : "text/plain" });
	res.write(msg);
	res.end();
};

exports.sendJSON = function(res, JSONmsg) {
	console.log("sendJSON");
	res.writeHead(200, { "Content-type" : "application/json" });
	res.write(JSON.stringify(JSONmsg));
	res.end();
};

exports.sendError = function(res, errorMsg) {
	res.writeHead(404, { "content-type" : "text/plain" });
	res.write(errorMsg);
	res.end();
};
