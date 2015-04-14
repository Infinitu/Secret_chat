/* server.js */

var http = require("http"),
	formidable = require("formidable"),
	fieldParser = require("./fieldParser.js");

function onRequest(req, res) {
	var form = new formidable.IncomingForm();
	fieldParser.fieldParse(req, res, form);
}

var server = http.createServer(onRequest);
server.listen(8080);

console.log("Server Start!");