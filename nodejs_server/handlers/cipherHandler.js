/* cipherHandler.js */

var crypto    = require("crypto"),
	dbHandler = require("./dbHandler");

var ALGORITHM       = "aes-128-cbc",
	CLEAR_ENCODING  = "utf8",
	CIPHER_ENCODING = "base64";
	
exports.encryptData = function (data, accessToken, callback) {
	_findUserId(accessToken, function(err, info) {
		var key = "";
		key += info._id;

		var _128BitData = data + "\0\0\0\0\0\0\0\0";  // 96 bit string 을 128 bit String 으로 변환
		var cipher = crypto.createCipher(ALGORITHM, key);
		var cipherChunks  = "";
		cipherChunks  = cipher.update(_128BitData, CLEAR_ENCODING, CIPHER_ENCODING);
	    cipherChunks += cipher.final(CIPHER_ENCODING);
		
	    callback(err, cipherChunks);
	});
};

exports.decryptData = function (encryptedData, accessToken, callback) {
	_findUserId(accessToken, function(err, info) {
		var key = "";
		key += info._id;
		
		var decipher = crypto.createDecipher(ALGORITHM, key);
		var decryptedData = decipher.update(encryptedData, CIPHER_ENCODING, CLEAR_ENCODING);
		decryptedData = decryptedData.slice(0, 24); // 128 Bit string 을 96 bit string 으로 변환
		
	    callback(err, decryptedData);
	});
};

function _findUserId(accessToken, callback) {
	var where   = { "accessToken" : accessToken };
	var options = { "_id" : 1 };
	
	dbHandler.findDb(where, options, callback);
}
