/* inappropriateAccessFilter.js */

var router = require("./router");

exports.dataFilter = (function() {
	var contents = {};	
	
	function dataFilter(res, pathname, method, incomingData) {
		contents = _JSONDataFileter(incomingData);
		router.route(res, pathname, method, contents);
	}
	
	return dataFilter;
})();

function _JSONDataFileter(incomingData) {
	var contents = {};
	
	if (incomingData.accessToken)
		contents.accessToken = incomingData.accessToken;
	
	if (incomingData.nickName)
		contents.nickName = incomingData.nickName;
	
	if (incomingData.age)
		contents.age = incomingData.age;
	
	if (incomingData.gender)
		contents.gender = incomingData.gender;
	
	if (incomingData.bloodType)
		contents.bloodType = incomingData.bloodType;
	
	if (incomingData.friends)
		contents.friends = incomingData.friends;
	
	if (incomingData.nickNameTag)
		contents.nickNameTag = incomingData.nickNameTag;
	
	return contents;
}
