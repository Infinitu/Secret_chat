/* redisDbHandler.js */

var redis  = require("redis");
var client = redis.createClient(6379, "127.0.0.1");
// client.auth("ginger2015");

exports.setNickNameTag = function(nickNameTag, id, callback) {
	client.set(nickNameTag, id, function(err) {
		client.expire(nickNameTag, 3600);
		callback(err);
	});
};

exports.getFriendId = function(key, callback) {
	client.get(key, function(err, friendId) {
		console.log("find redis data:", friendId);
		if (friendId == null)
			err = 1;
		
		callback(err, friendId);
	});
};

exports.isExistingNickNameTag = function(nickNameTag, callback) {
	client.exists(nickNameTag, function(err, isExisting) {
		callback(err, isExisting);
	});
};

client.on("error", function(err) {
	console.log("Redis error event - " + client.host + ":" + client.port + " - " + err);
});
