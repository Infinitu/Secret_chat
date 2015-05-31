/* imageHandler.js */

var fs             = require("fs"),
	path           = require("path"),
	msgHandler     = require("./msgHandler"),
	dbHandler      = require("./dbHandler"),
	globalVariable = require("./globalVariable");

var IP_ADDRESS = globalVariable.IP_ADDRESS;
var PROFILE_FOLDER = globalVariable.PROFILE_FOLDER;

exports.uploadImage = function(res, contents) {
	var newImageUrl = PROFILE_FOLDER + "upload_image_" + contents.accessToken + path.extname(contents.imageUrl);
	
	fs.rename(contents.imageUrl, newImageUrl, function(err) {
    	if (err) {
    		msgHandler.sendError(res);
			return ;
    	}
    	
		var downloadAddress = IP_ADDRESS + newImageUrl.replace("./", "/");
		
		msgHandler.sendString(res, downloadAddress);
	});
};

exports.showImage = function(res, contents) {
	var filePath = PROFILE_FOLDER + contents.imageName;
	
	fs.readFile(filePath, function(err, data) {
		msgHandler.sendFile(res, data, filePath);
	});
};

