/* cipherHandler.js */

var crypto = require("crypto");
var test = require("./cipherHandler");

exports.encryptData = function (data, key, callback) {
	console.log("start encrypt");
	var cipher = crypto.createCipher("aes-128-cbc", key);
	var encryptedData = cipher.update(data, "utf8", "base64");
	encryptedData += cipher.final("base64");
	callback(encryptedData);
};

exports.decryptData = function (encryptedData, key) {
	var decipher = crypto.createDecipher("aes-128-cbc", key);
	var decryptedData = decipher.update(encryptedData, "base64", "utf8");
	
	return decryptedData;
};

