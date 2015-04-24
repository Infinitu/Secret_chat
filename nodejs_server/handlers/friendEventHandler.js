/* friendEventHandler.js */

var ObjectID      = require("mongodb").ObjectID,
	msgHandler    = require("./msgHandler"),
	dbHandler     = require("./dbHandler"),
	cipherHandler = require("./cipherHandler");

exports.find = function(res, contents) {
	_findFriend("nickNameTag", contents.nickNameTag, function(err, friendInfo) {
		if (err) msgHandler.sendError(res, "find friend error!");
		
		cipherHandler.encryptData(friendInfo._id, contents.accessToken, function(err, encryptedId) {
			if (err) msgHandler.sendError(res, "encrypt friendID error!");
			
			friendInfo._id = encryptedId;
			msgHandler.sendJSON(res, friendInfo);
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
			if (err) msgHandler.sendError(res, "decrypt friendId error!");
			
			_findFriend("_id", decryptedId, function(err, friendInfo) {
				if (err) msgHandler.sendError(res, "find friend error!");
				
				friendsInfo.push(friendInfo);
				numberOfFriendInfo++;
				
				if (numberOfFriendInfo === numberOfFriends)
					msgHandler.sendJSON(res, friendsInfo);
			});
		});
	}
};

function _findFriend(field, value, callback) {
	if (field === "nickNameTag") {
		var where   = { "nickNameTag" : value };
		var options = { "_id" : 1, "nickName"  : 1, "gender" : 1, 
						"userCharacter" : 1 , "imageUrl" : 1 };
	} else {
		var where   = { "_id" : new ObjectID(value) };
		var options = { "_id" : 0, "nickName"  : 1, "gender" : 1, 
						"userCharacter" : 1 , "imageUrl" : 1 };
	}

	dbHandler.findDb(where, options, callback);
}
