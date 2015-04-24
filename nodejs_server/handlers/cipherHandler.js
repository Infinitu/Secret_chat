/* cipherHandler.js */

var crypto    = require("crypto"),
	dbHandler = require("./dbHandler");

var algorithm      = "aes-128-cbc",
	clearEncoding  = "utf8",
	cipherEncoding = "base64";
	
exports.encryptData = function (data, accessToken, callback) {
	_findUserId(accessToken, function(err, info) {
		if (err) throw err;
		
		var key = "";
		key += info._id;
		
		var cipher = crypto.createCipher(algorithm, key);
		var cipherChunks  = "";
		var _128BitData = data + "\0\0\0\0\0\0\0\0";
		
		cipherChunks  = cipher.update(_128BitData, clearEncoding, cipherEncoding);
	    cipherChunks += cipher.final(cipherEncoding);
		
	    callback(err, cipherChunks);
	});
};

exports.decryptData = function (encryptedData, accessToken, callback) {
	_findUserId(accessToken, function(err, info) {
		if (err) throw err;
		
		var key = "";
		key += info._id;
		
		var decipher = crypto.createDecipher(algorithm, key);
		var decryptedData = decipher.update(encryptedData, cipherEncoding, clearEncoding);
		decryptedData = decryptedData.slice(0, 24); 
		
		console.log("decryptedData: ", decryptedData);
	    callback(err, decryptedData);
	});
};

function _findUserId(accessToken, callback) {
	var where   = { "accessToken" : accessToken };
	var options = { "_id" : 1 };
	
	dbHandler.findDb(where, options, callback);
}
