/* friendEventHandler.js */

var ObjectID      = require("mongodb").ObjectID,
	fs            = require("fs"),
	msgHandler    = require("./msgHandler"),
	dbHandler     = require("./dbHandler"),
	redisHandler  = require("./redisDbHandler"),
	cipherHandler = require("./cipherHandler");

var IP_ADDRESS = "http://192.168.1.108:8080";

exports.find = function(res, contents) {
	_findFriendId(contents.nickNameTag, function(err, friendId) {
		if (err) msgHandler.sendError(res);
		
		_findFriend(friendId, function(err, friendInfo) {
			if (err) msgHandler.sendError(res);
			
			cipherHandler.encryptData(friendInfo._id, contents.accessToken, function(err, encryptedId) {
				if (err) msgHandler.sendError(res);
				
				friendInfo.imageUrl = IP_ADDRESS + friendInfo.imageUrl.replace("./", "/");
				friendInfo._id = encryptedId;
				msgHandler.sendJSON(res, friendInfo);
			});
		});
	});
};

exports.read = function(res, contents) {
	var friendsInfo = [];
	var friends     = contents.friends.split(",");
	var numberOfFriends    = friends.length;
	var numberOfFriendInfo = 0;
	
	for (var i = 0; i < numberOfFriends; i++) {
		cipherHandler.decryptData(friends[i], contents.accessToken, function(err, decryptedId) {
			if (err) msgHandler.sendError(res);
			
			_findFriend(decryptedId, function(err, friendInfo) {
				if (err) msgHandler.sendError(res);
				
				friendsInfo.push(friendInfo);
				numberOfFriendInfo++;
				
				if (numberOfFriendInfo === numberOfFriends)
					msgHandler.sendJSON(res, friendsInfo);
			});
		});
	}
};

exports.showImage = function(res, contents) {
	var filePath = "./profileImages" + "/" + contents.imageName;

	fs.readFile(filePath, function(err, data) {
		msgHandler.sendFile(res, data, filePath);
	});
};

function _findFriendId(nickNameTag, callback) {
	redisHandler.getFriendId(nickNameTag, callback);
}

function _findFriend(id, callback) {
	var where   = { "_id" : new ObjectID(id) };
	var options = { "_id" : 0, "nickName"  : 1, "gender" : 1, "bloodType" : 1,
						"imageUrl" : 1, "chatLevel" : 1, "gentle" : 1, "cool" : 1, "pervert" : 1, "common" : 1};

	dbHandler.findDb(where, options, callback);
}
