/* msgHandler.js */

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

exports.sendJSON = function(res, JSONmsg) {
	res.writeHead(200, { "Content-type" : "application/json" });
	res.write(JSON.stringify(JSONmsg));
	res.end();
};
