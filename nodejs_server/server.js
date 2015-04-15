/* server.js */

var http = require("http"),
	formidable = require("formidable"),
	incomingDataParser = require("./incomingDataParser");

function onRequest(req, res) {
	var form = new formidable.IncomingForm();
	incomingDataParser.dataParse(req, res, form);
}

var server = http.createServer(onRequest);
server.listen(8080);

console.log("Server Start!");