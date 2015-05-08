/* userInfoHandler.js */

var	fs   = require("fs"),
	path = require("path"),
	hat  = require("hat"),   // accessToken 생성 module
	rack = hat.rack(),       // accessToken 생성 변수 -> 사용방법 : rack(); = 중복없는 token 생성
	msgHandler    = require("./msgHandler"),
	dbHandler     = require("./dbHandler"),
	cipherHandler = require("./cipherHandler"); 

var PROFILE_FOLDER = "./profileImages/";
	
exports.join = function(res, contents) {
	contents.chatLevel    = 0;
    contents.gentle       = 0;
    contents.cool         = 0;
    contents.pervert      = 0;
    contents.common       = 0;
    contents.joinDate     = new Date();
    contents.accessToken  = _getAccessToken();       // accessToken 생성
	contents.imageUrl     = _getImageUrl(contents);
	
	_insertUserProfile(contents, function(err, userInfo) {
    	if (err) msgHandler.sendError(res);
    	
    	cipherHandler.encryptToken(contents.accessToken, function(token) {
    		msgHandler.sendString(res, token);
    	});
	});
};

exports.read = function(res, contents) {
	_findUserProfile(contents.accessToken, function(err, userInfo) {
		if (err) msgHandler.sendError(res);
		
		msgHandler.sendJSON(res, userInfo);
	});
};

exports.update = function(res, contents) {
	_updateUserProfile(contents.accessToken, contents, function(err) {
		if (err) msgHandler.sendError(res);
		
		exports.read(res, contents);
	});
};

exports.remove = function(res, contents) {
	_removeUserProfile(contents.accessToken, function(err) {
		if (err) msgHandler.sendError(res);
		
		var message = "deleted!";
    	msgHandler.sendString(res, message);
	});
};

function _getAccessToken() {
	var accesstoken = rack();
	
	return accesstoken;
}

function _getImageUrl(contents) {
	if (!contents.imageUrl)
		return null;
	
	var newImageUrl = PROFILE_FOLDER + contents.accessToken + "_profile_image" + path.extname(contents.imageUrl);
	fs.rename(contents.imageUrl, newImageUrl);
	
	return newImageUrl;
}

function _insertUserProfile(contents, callback) {
	dbHandler.insertDb(contents, callback);
}

function _findUserProfile(accessToken, callback) {
	var where   = { "accessToken" : accessToken };
	var options = { "_id" : 0, "nickName" : 1, "birthYear" : 1, "gender" : 1, "bloodType" : 1,
					"chatLevel" : 1, "gentle" : 1, "cool" : 1, "pervert" : 1, "common" : 1 };
	
	dbHandler.findDb(where, options, callback);
}

function _updateUserProfile(accessToken, contents, callback) {
	if (contents.birthYear)
		contents.age = _getAge(contents.birthYear);
	
	if (contents.nickName)
		contents.nickNameTag = contents.nickName + "1";
	
	var where    = { "accessToken" : accessToken };
	var operator = { $set : contents };
	
	dbHandler.updateDb(where, operator, callback);
}

function _removeUserProfile(accessToken, callback) {
	var where = { "accessToken" : accessToken };
	
	dbHandler.removeDb(where, callback);
}
