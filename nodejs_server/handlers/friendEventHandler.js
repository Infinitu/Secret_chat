/* friendEventHandler.js */

var ObjectId      = require("mongodb").ObjectID,
	msgHandler    = require("./msgHandler"),
	dbHandler     = require("./dbHandler"),
	cipherHandler = require("./cipherHandler");

exports.find = function(res, contents) {
	_findFriend("nickNameTag", contents.nickNameTag, function(err, friendInfo) {
		if (err)
			msgHandler.sendError(res, "find friend error!");
		
		cipherHandler.encryptData(friendInfo._id, contents.accessToken, function(err, encryptedId) {
			if (err)
				msgHandler.sendError(res, "encrypt friendID error!");
			
			friendInfo._id = encryptedId;
			msgHandler.sendJSON(res, friendInfo);
		});
	});
};

exports.add = function(res, contents) {
	_addFriend(contents.accessToken, contents.friendId, function(err) {
		if (err)
			msgHandler.sendError(res, "find userId error!");
		
	    	var message = "added friend!";
	    	msgHandler.sendString(res, message);
	});
};

exports.read = function(res, contents) {
	_readFriends(contents.accessToken, function(err, userFriends) {
		if (err)
			msgHandler.sendError(res, "read friends info error!");
		
		var friendsInfo = [];
		var numberOfFriends = userFriends.friends.length;
		var numberOfInfo = 0
		
		for (var i = 0; i < numberOfFriends; i++) {
			cipherHandler.decryptData(userFriends.friends[numberOfInfo], contents.accessToken, function(err, decryptedId) {
				if (err) msgHandler.sendError(res, "decrypt friendId error!");
				
				_findFriend("_id", decryptedId, function(err, friendInfo) {
					if (err) msgHandler.sendError(res, "find friend error!");
					
					friendsInfo.push(friendInfo);
					numberOfInfo++;
					
					if (numberOfInfo === numberOfFriends)
						msgHandler.sendJSON(res, friendsInfo);
				});
			});
		}
	});
};

exports.remove = function(res, contents) {
	_removeFriend(contents.accessToken, contents.friendId, function(err) {
		if (err)
			msgHandler.sendError(res, "add friend error!");
		
    	var message = "removed" + contents.friendId;
    	msgHandler.sendString(res, message);
	});
};

function _findFriend(field, value, callback) {
	if (field == "nickNameTag") {
		var where   = { "nickNameTag" : value };
		var options = { "_id" : 1, "nickName"  : 1, "gender" : 1, 
						"userCharacter" : 1 , "imageUrl" : 1 };
	} else {
		var where   = { "_id" : new ObjectId(value) };
		var options = { "_id" : 0, "nickName"  : 1, "gender" : 1, 
						"userCharacter" : 1 , "imageUrl" : 1 };
	}

	dbHandler.findDb(where, options, callback);
}

function _addFriend(accessToken, friendId, callback) {
	var where   = { "accessToken" : accessToken };
	var options = { $push : { "friends" : friendId } };

	dbHandler.updateDb(where, options, callback);
}

function _readFriends(accessToken, callback) {
	var where   = { "accessToken" : accessToken };
	var options = { "_id" : 0, "friends" : 1 };
	
	dbHandler.findDb(where, options, callback);
}

function _removeFriend(accessToken, friendId, callback) {
	var where   = { "accessToken" : accessToken };
	var options = { $pull : { "friends" : friendId } };
	
	dbHandler.updateDb(where, options, callback);
}
