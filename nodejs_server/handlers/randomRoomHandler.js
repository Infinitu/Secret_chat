
/* randomRoomHandler.js */

var	ObjectID       = require("mongodb").ObjectID, 
	randomkey      = require("random-key"),
	dbHandler      = require("./dbHandler"),
	msgHandler     = require("./msgHandler"),
	redisHandler   = require("./redisDbHandler"),
	globalVariable = require("./globalVariable");

var IP_ADDRESS = globalVariable.IP_ADDRESS;

exports.getUserInfoInRandomRoom = function(res, contents) {
	_findUserInRandomRoom(contents.roomNumber, function(err, friendIds) {
		if (err) {
			msgHandler.sendError(res);
			return ;
		}
		
		var friendId = [];
		friendId = friendIds.split("\\");
		
		_findFriendByAccessToken(contents.accessToken, function(err, searchedId) {
			if (err) {
				msgHandler.sendError(res);
				return ;
			}
			
			var friendIdForSearch = "";
			
			if (searchedId._id == friendId[0])
				friendIdForSearch = friendId[1];
			
			else if (searchedId._id == friendId[1])
				friendIdForSearch = friendId[0];
			
			else {
				msgHandler.sendError(res);
				return ;
			}
			
			_findFriendById(friendIdForSearch, function(err, searchedId) {
				if (err) {
					msgHandler.sendError(res);
					return ;
				}
				searchedId.imageUrl = IP_ADDRESS + searchedId.imageUrl.replace("./", "/");
				msgHandler.sendJSON(res, searchedId);
			});
		});
	});
};

function _findUserInRandomRoom(roomNumber, callback) {
	redisHandler.getFriendId(roomNumber, callback);
}

function _findFriendByAccessToken(accessToken, callback) {
	var where   = { "accessToken" : accessToken};
	var options = { "_id" : 1 };

	dbHandler.findDb(where, options, callback);
}

function _findFriendById(id, callback) {
	var where   = { "_id" : new ObjectID(id) };
	var options = { "_id" : 0, "nickName"  : 1, "gender" : 1, "bloodType" : 1, "age" : 1,
					"imageUrl" : 1, "chatLevel" : 1, "gentle" : 1, "cool" : 1, "pervert" : 1, "common" : 1};

	dbHandler.findDb(where, options, callback);
}