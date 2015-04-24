/* fileHandler.js */

var fs = require("fs");

exports.renameFile = function (oldImageUrl, newImageUrl) {
	fs.rename(oldImageUrl, newImageUrl);
};
