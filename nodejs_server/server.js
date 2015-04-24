/* server.js */

var http       = require("http"),
	formidable = require("formidable"),
	dataParser = require("./incomingDataParser");

function onRequest(req, res) {
	var form = new formidable.IncomingForm();
	form.uploadDir = "./profileImages";
	form.keepExtensions = true;
	form.maxFieldsSize  = 10 * 1024 * 1024;  // 최대 보낼 수 있는 파일 용량 10 mb

	dataParser.dataParse(req, res, form);
}

var server = http.createServer(onRequest);
server.listen(8080);

console.log("Server Start!");
