/* friendEventHandler.js */

var objectId   = require("mongodb").ObjectID,
	msgHandler = require("./msgHandler"),
	dbHandler  = require("./dbHandler");

exports.find = function(res, contents) {
	_findFriend("nickNameTag", contents.nickNameTag, function(err, friendInfo) {
		if (err)
			msgHandler.sendError(res, "find friend error!");
		
		msgHandler.sendJSON(res, friendInfo);
	});
};
    
exports.add = function(res, contents) {
	_addFriend(contents.accessToken, contents.friendId, function(err, friendId) {
		if (err)
			msgHandler.sendError(res, "add friend error!");
		
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
		var numberOfInfo = 0;
		
		for (var i = 0; i < numberOfFriends; i++) {
			_findFriend("_id", userFriends.friends[i], function(err, friendInfo) {
				if (err)
					msgHandler.sendError(res, "find friend error!");
				
				friendsInfo.push(friendInfo);
				numberOfInfo++;
				
				if (numberOfInfo === numberOfFriends)
					msgHandler.sendJSON(res, friendsInfo);
			});
		}
	});
};

exports.remove = function(res, contents) {
	_removeFriend(contents.accessToken, contents.friendId, function(err) {
		var friendId = contents.friendId;
		
		if (err)
			msgHandler.sendError(res, "add friend error!");
		
    	var message = "removed" + friendId;
    	msgHandler.sendString(res, message);
	});
};

function _findFriend(field, value, callback) {
	var where = {};
	
	if (field === "nickNameTag")
		where = { "nickNameTag" : value };
	
	else if (field === "_id")
		var where = { "_id" : new objectId(value) };	
	
	var options = { "_id" : 1, "nickName"  : 1, "gender" : 1, 
					"age" : 1, "character" : 1 };

	dbHandler.findDb(where, options, callback);
}

function _readFriends(accessToken, callback) {
	var where = { "accessToken" : accessToken };
	var options = { "_id" : 0, "friends" : 1 };  // _id, accessToken을 제외한 다른 정보만 return
	
	dbHandler.findDb(where, options, callback);
}

function _addFriend(accessToken, friendId, callback) {
	var where = { "accessToken" : accessToken };
	var operator = { $addToSet : { "friends" : friendId } };
	
	dbHandler.updateDb(where, operator, callback);
}

function _removeFriend(friendId, callback) {
	var id = require("mongodb").ObjectID(friendId);
	var where = { "_id" : id };
	
	dbHandler.removeDb(where, callback);
}
