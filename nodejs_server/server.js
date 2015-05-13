/* server.js */

var fs         = require("fs"),
	https      = require("http"),
	formidable = require("formidable"),
	dataParser = require("./incomingDataParser");

var HTTPS_PORT = 8080;

var httpsOptions = {
	key  : fs.readFileSync("./key/key.pem"),
	cert : fs.readFileSync("./key/cert.pem")
};

function onRequest(req, res) {
	var incomingData = new formidable.IncomingForm();
	incomingData.uploadDir = "./profileImages";
	incomingData.keepExtensions = true;
	incomingData.maxFieldsSize  = 5 * 1024 * 1024;  // 최대 보낼 수 있는 파일 용량 5 mb

	dataParser.dataParser(req, res, incomingData);
}

var server = https.createServer(onRequest);
server.listen(HTTPS_PORT);

console.log("Server Start!");
