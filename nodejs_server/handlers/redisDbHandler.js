/* redisDbHandler.js */

var redis  = require("redis");
var client = redis.createClient();

exports.setNickNameTag = function(nickNameTag, id, callback) {
	client.set(nickNameTag, id, function(err) {
		client.expire(nickNameTag, 3600);
		callback(err);
	});
};

exports.getFriendId = function(nickNameTag, callback) {
	client.get(nickNameTag, function(err, friendId) {
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
