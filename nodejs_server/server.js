/* server.js */

var fs         = require("fs"),
	https      = require("https"),
	formidable = require("formidable"),
	dataParser = require("./incomingDataParser");

var HTTPS_PORT = 8080;

var httpsOptions = {
	key  : fs.readFileSync("./key/key.pem"),
	cert : fs.readFileSync("./key/cert.pem")
};

function onRequest(req, res) {
	var form = new formidable.IncomingForm();
	form.uploadDir = "./profileImages";
	form.keepExtensions = true;
	form.maxFieldsSize  = 10 * 1024 * 1024;  // 최대 보낼 수 있는 파일 용량 10 mb

	dataParser.dataParse(req, res, form);
}

var server = https.createServer(httpsOptions, onRequest);
server.listen(HTTPS_PORT);

console.log("Server Start!");
