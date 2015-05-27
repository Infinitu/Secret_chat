/* nickNameTagHandler.js */

var randomkey    = require("random-key"),
	dbHandler    = require("./dbHandler"),
	msgHandler   = require("./msgHandler"),
	redisHandler = require("./redisDbHandler");

var RAMDOMDIGITS = 5;

exports.getNickNameTag = function(res, contents) {
	_getIdAndNickName(contents.accessToken, function(err, userInfo) {
		if (err) {
			msgHandler.sendError(res);
			return ;
		}
		
		console.log(userInfo);
		
		var nickNameTag = userInfo.nickName + randomkey.generateDigits(RAMDOMDIGITS);
		
		_isExistingNickNameTag(nickNameTag, function(err, isExisting) {
			if (err) msgHandler.sendError(res);
			
			if (isExisting) {
				exports.getNickNameTag(res, contents);
				return ;
			}
			
			_setNickNameTagInRedis(nickNameTag, userInfo._id, function(err) {
				if (err) msgHandler.sendError(res);
				
				msgHandler.sendJSON(res, nickNameTag);
			});
		});
	});
};

function _getIdAndNickName(accessToken, callback) {
	var where   = { "accessToken" : accessToken };
	var options = { "_id" : 1, "nickName" : 1 };
	
	dbHandler.findDb(where, options, callback);
}

function _isExistingNickNameTag(nickNameTag, callback) {
	redisHandler.isExistingNickNameTag(nickNameTag, callback);
}

function _setNickNameTagInRedis(nickNameTag, id, callback) {
	redisHandler.setNickNameTag(nickNameTag, id, callback);
}
