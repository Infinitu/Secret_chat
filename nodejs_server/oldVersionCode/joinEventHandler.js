/* joinEventHandler.js */

var hat = require("hat"), // accessToken 생성 module
	rack = hat.rack();    // accessToken 생성 변수 -> 사용방법 : rack(); = 중복없는 token 생
	msgHandler = require("./msgHandler"),
	dbHandler = require("./dbHandler");
	
exports.create = function(res, contents) {
    	contents.accessToken = _getAccessToken();   // accessToken 생성
    	contents.age = _getAge(contents.birthYear); // 생년월일을 토대로 나이 계산
        contents.level = 0;
        contents.character = { "gentle" : 0, "cool" : 0, "pervert" : 0, "common" : 0 };
        contents.joinDate = new Date();            // 가입일 생성
        
        dbHandler.insertDb(contents, function(err) {
        	if (err)
        		msgHandler.sendError("deviceId is already existed"); // **deviceId 중복 확인 필요
        	
        	msgHandler.sendJoinConfirmed(res, contents.accessToken);
        });
};

function _getAccessToken() {
	var accesstoken = rack();
	
	return accesstoken;
}

function _getAge(birthYear) {
	var date = new Date();
	var presentYear = date.getFullYear();
	var age = presentYear - birthYear + 1;
	
	return age;
}