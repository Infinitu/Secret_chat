/* handlers/joinEventHandler.js */

var mongodb = require('mongodb'),
	server = new mongodb.Server('localhost', 27017, {});
	db = new mongodb.Db('secretChat', server, {w: 1});
	collection = db.collection("members"),
	url = require("url"),
	querystring = require("querystring"),
	fs = require("fs"),
	hat = require("hat"), // accessToken 생성 module
	rack = hat.rack();    // accessToken 생성 변수 -> 사용방법 : rack(); = 중복없는 token 생
    formidable = require('formidable');
	
var UPLOAD_FOLDER = "./images/";

exports.create = function(req, res, form) {

	var uploadFile,
    	content = {};
	
	form.uploadDir = UPLOAD_FOLDER;
	form.keepExtensions = true;
	form.maxFieildsSize = 5 * 1024 * 1204; // 최대 파일 크기 5MB
	
	form.on('field', function(key, value) {
		content[key] = value;
	    })
	    .on('file', function(key, file) {
	    	var isImage = checkImageFileType(file); // image 파일이 아니면 error
	    	
	    	if (isImage === false)
	    		sendErrorMessage(res);
	    	else
	    		uploadFile = file;	
	    })
	    .on('end', function() {
	    	// 회원 ID 는 mongodb _Id 로 사용
	    	content.age = getAge(content.birthYear);  // 생년월일을 토대로 나이 계산
	        content.level = 0;
	        content.character = { "gentle" : 0, "cool" : 0, "pervert" : 0, "common" : 0 };
	        content.joinDate = new Date();            // 가입일 생성
	    	content.accessToken = getAccessToken();   // accessToken 생성
	        content.images = getImagePath(form.uploadDir, content.accessToken, uploadFile);
	        fs.rename(uploadFile.path, content.images);
	        
	        insertDb(content);
        }); 

	form.parse(req, function(err, fields, files) {
		if(err) {
			res.writeHead(404, { "content-type" : "text/plain" });
			res.write("error");
			res.end();
		}
	    res.writeHead(200, { "Content-type" : "text/plain" });
		res.write("good" + "|" + content.accessToken);
	    res.end();
	});
};

function sendErrorMessage(res) {
	res.writeHead(404, { "content-type" : "text/plain" });
	res.write("error");
	res.end();
}

function checkImageFileType(file) {
	var fileType = file.type.split("/");
	
	if (fileType[0] === "image")
		return true;

	return false;
}

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

function getImagePath(uploadDir, accessToken, file) {
	var fileType = file.type.split("/");
	var imagePath = uploadDir + accessToken + "_profile_image" + "." + fileType[1];
	
	return imagePath;
}

function insertDb(content) {
    db.open(function(err) {
        if (err) throw err;
        collection.insert(content, function(err, data) {
            if (err) throw err; // deviceId 중복 체크에 대한 Error 처리 필
            
            console.log("insert Data: ", data.result);
            console.log(JSON.stringify(content));
            db.close();
        });
    });
}