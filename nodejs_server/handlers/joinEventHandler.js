/* joinEventHandler.js */

var hat = require("hat"), // accessToken 생성 module
	rack = hat.rack();    // accessToken 생성 변수 -> 사용방법 : rack(); = 중복없는 token 생
	msgHandler = require("./msgHandler.js"),
	dbHandler = require("./dbHandler.js");
	
exports.create = function(req, res, contents) {
    	contents.accessToken = getAccessToken();   // accessToken 생성
    	contents.age = getAge(contents.birthYear); // 생년월일을 토대로 나이 계산
        contents.level = 0;
        contents.character = { "gentle" : 0, "cool" : 0, "pervert" : 0, "common" : 0 };
        contents.joinDate = new Date();            // 가입일 생성
        
        dbHandler.insertDb(contents, res, msgHandler.sendJoinConfirmed); // db 저장 후 callback으로 가입 확인 메세지 발
};

function getAccessToken() {
	var accesstoken = rack();
	
	return accesstoken;
}

function getAge(birthYear) {
	var date = new Date();
	var presentYear = date.getFullYear();
	var age = presentYear - birthYear + 1;
	
	return age;
}