/* fileHandler.js */

var fs = require("fs");

exports.renameFile = function (oldImageUrl, newImageUrl, callback) {
	fs.rename(oldImageUrl, newImageUrl, function(err) {
		callback(err);
	});
};
