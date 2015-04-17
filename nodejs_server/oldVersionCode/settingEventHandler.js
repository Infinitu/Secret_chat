/* settingEventHandler.js */

var msgHandler = require("./msgHandler"),
	dbHandler = require("./dbHandler");
    

exports.read = function(res, contents) {
	_findUserProfile(contents.accessToken, function(err, results) {
		if(err)
			msgHandler.sendError(res, "find error");
		
		msgHandler.sendJSON(res, results);
	});
};

exports.update = function(res, contents) {
	_updateUserProfile(contents.accessToken, contents, function(err) {
		if(err)
			msgHandler.sendError(res, "find error");
		
		exports.read(req, res, contents);
	});
};

function _findUserProfile(accessToken, callback) {
	var where = { "accessToken" : accessToken };
	var options = { "_id" : 0, "nickName" : 1, "birthYear" : 1, "gender" : 1,
					"bloodType" : 1, "level" : 1, "character" : 1 };
	
	dbHandler.findDb(where, options, callback);
}

function _updateUserProfile(accessToken, contents, callback) {
	var where = { "accessToken" : accessToken };
	var operator = { $set : contents };
	
	dbHandler.updateDb(where, operator, callback);
}