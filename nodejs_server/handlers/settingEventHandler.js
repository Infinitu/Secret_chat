/* settingEventHandler.js */

var msgHandler = require("./msgHandler.js"),
	dbHandler = require("./dbHandler.js");
    

exports.read = function(req, res, contents) {
	_findUserProfile(contents.accessToken, function(err, results) {
		if(err)
			msgHandler.sendError(res, "find error");
		
		msgHandler.sendJSON(res, results);
	});
};

exports.update = function(req, res, contents) {
	
};

function _findUserProfile(accessToken, callback) {
	var operator = { "accessToken" : accessToken };
	var options = { "_id" : 0, "nickName" : 1, "birthYear" : 1, "gender" : 1,
					"bloodType" : 1, "level" : 1, "character" : 1 };
	
	dbHandler.findDb(operator, options, callback);
}