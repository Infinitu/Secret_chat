/* userInfoHandler.js */

var	hat  = require("hat"),   // accessToken 생성 module
	rack = hat.rack(),       // accessToken 생성 변수 -> 사용방법 : rack(); = 중복없는 token 생성
	mime = require("mime"),
	msgHandler  = require("./msgHandler"),
	dbHandler   = require("./dbHandler"),
	fileHandler = require("./fileHandler");

var PROFILE_FOLDER = "./profileImages/";
	
exports.join = function(res, contents) {
	contents.age = _getAge(contents.birthYear); // 생년월일을 토대로 나이 계산
	contents.nickNameTag = contents.nickName + "1";
	contents.friends = [];
    contents.level = 0;
    contents.userCharacter = { "gentle" : 0, "cool" : 0, "pervert" : 0, "common" : 0 };
    contents.joinDate = new Date();             // 가입일 생성
	contents.accessToken = _getAccessToken();   // accessToken 생성
	contents.imageUrl = _getImageUrl(contents);
    
	_insertUserProfile(contents, function(err) {
    	if (err)
    		msgHandler.sendError("insert user info error!");
    	
    	var message = {};
    	
    	message.accessToken = contents.accessToken;
    	message.imageUrl    = contents.imageUrl;
    	
    	msgHandler.sendJSON(res, message);
	});
};

exports.read = function(res, contents) {
	_findUserProfile(contents.accessToken, function(err, userInfo) {
		if (err)
			msgHandler.sendError(res, "find user info error!");
		
		msgHandler.sendJSON(res, userInfo);
	});
};

exports.update = function(res, contents) {
	_updateUserProfile(contents.accessToken, contents, function(err) {
		if (err)
			msgHandler.sendError(res, "update user info error!");
		
		exports.read(res, contents);
	});
};

exports.remove = function(res, contents) {
	_removeUserProfile(contents.accessToken, function(err) {
		if (err)
			msgHandler.sendError(res, "Delete user info error!");
		
		var message = "deleted!";
    	msgHandler.sendString(res, message);
	});
};

function _getAccessToken() {
	var accesstoken = rack();
	
	return accesstoken;
}

function _getAge(birthYear) {
	var date = new Date();
	var presentYear = date.getFullYear();
	var age = presentYear - birthYear + 1;
	
	return age;
}

function _getImageUrl(contents) {
	if (!contents.imageUrl)
		return null;
	
	var oldImageUrl = contents.imageUrl;
	var newImageUrl = PROFILE_FOLDER + contents.accessToken + "_profile_image."
					  + mime.extension(mime.lookup(oldImageUrl));

	fileHandler.renameFile(oldImageUrl, newImageUrl, function(err) {
		if (err)
			msgHandler.sendError(res, "rename download file error!");
	});
	
	return newImageUrl;
}

function _insertUserProfile(contents, callback) {
	dbHandler.insertDb(contents, callback);
}

function _findUserProfile(accessToken, callback) {
	var where = { "accessToken" : accessToken };
	var options = { "_id" : 0, "nickName" : 1, "birthYear" : 1, "gender" : 1,
					"bloodType" : 1, "level" : 1, "userCharacter" : 1, "imageUrl" : 1 };
	
	dbHandler.findDb(where, options, callback);
}

function _updateUserProfile(accessToken, contents, callback) {
	var where = { "accessToken" : accessToken };
	var operator = { $set : contents };
	
	dbHandler.updateDb(where, operator, callback);
}

function _removeUserProfile(accessToken, callback) {
	var where = { "accessToken" : accessToken };
	
	dbHandler.removeDb(where, callback);
}