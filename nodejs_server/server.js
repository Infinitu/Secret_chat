/* server.js */

var http = require("http"),
	route = require("./route.js"),
	url = require("url"),
	formidable = require('formidable');

function onRequest(req, res) {
	var form;

	form = new formidable.IncomingForm();
	route.route(req, res, form);
}

var server = http.createServer(onRequest);
server.listen(8080);

console.log("Server Start!");