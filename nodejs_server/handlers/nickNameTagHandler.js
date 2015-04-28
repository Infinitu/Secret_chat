/* nickNameTagHandler.js */

var randomkey  = require("random-key"),
	scheduler  = require("node-schedule"),
	dbHandler  = require("./dbHandler"),
	msgHandler = require("./msgHandler");

var RAMDOMDIGITS = 5;

exports.getNickNameTag = function(res, contents) {
	console.log("start get nickNameTag!");
	var nickNameTag = contents.nickName + randomkey.generateDigits(RAMDOMDIGITS);
	
	_isOverlapped(nickNameTag, function(err, isOverlapped) {
		if (err) console.log("find nickNameTag Error!");
		
		if (isOverlapped) {
			exports.getNickNameTag(res, contents);
			return ;
		}
		
		var where    = { "accessToken" : contents.accessToken };
		var operator = { $set : { "nickNameTag" : nickNameTag } };
		
		dbHandler.updateDb(where, operator, function(err) {
			if (err) console.log("insert nickNameTag Error!");
			
			_deleteNickNameTagAfterHour(nickNameTag);
			msgHandler.sendJSON(res, nickNameTag);
		});
	});
};

function _isOverlapped(nickNameTag, callback) {
	var where   = { "nickNameTag" : nickNameTag };
    var options = { "nickNameTag" : 1 };
    
	dbHandler.findDb(where, options, function(err, data) {
		var isOverlapped = false;
		
		if (data)
			isOverlapped = true;
		
		callback(err, isOverlapped);
	});
}

function _deleteNickNameTagAfterHour(nickNameTag) {
	var date = new Date();
	date.setHours(date.getHours() + 1);
	
	var remove = scheduler.scheduleJob(date, function(){
		var where   = { "nickNameTag" : nickNameTag };
	    var options = { "accessToken": 1, "nickNameTag" : 1 };
	    
		dbHandler.findDb(where, options, function(err, data) {
			if (err) console.log("err delete nickNameTag Error!");
			
			if (data) {
				var _where    = { "accessToken" : data.accessToken };
				var _operator = { $unset : { "nickNameTag" : nickNameTag } };
				
				dbHandler.updateDb(_where, _operator, function(err) {
					if (err) console.log("delete nickNameTag Error!");
				});
			}
		}.bind(nickNameTag));
	});
}
