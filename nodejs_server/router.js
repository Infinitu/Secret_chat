/* route.js */

var userEventHandler   = require("./handlers/userEventHandler"),
	friendEventHandler = require("./handlers/friendEventHandler"),
	nickNameTagHandler = require("./handlers/nickNameTagHandler"),
	imageEventHandler  = require("./handlers/imageEventHandler"),
	randomRoomHandler  = require("./handlers/randomRoomHandler"),
	msgEventHandler    = require("./handlers/msgHandler");

exports.route = (function() {
	var handlers = { 
					 "/"			   : { GET  : msgEventHandler.helloSecretChat },
					 "/join"           : { POST : userEventHandler.join },
					 "/setting"        : { POST : userEventHandler.read, PUT : userEventHandler.update },
					 "/getTag"         : { POST : nickNameTagHandler.getNickNameTag },
					 "/addfriend"      : { POST : friendEventHandler.find },
					 "/main"           : { POST : friendEventHandler.read },
					 "/uninstall"      : { DELETE : userEventHandler.remove },
					 "/profileimages"  : { POST : imageEventHandler.uploadImage, GET : imageEventHandler.showImage },
					 "/randomroominfo" : { POST : randomRoomHandler.getUserInfoInRandomRoom }
	};
	
	function route(res, pathname, method, contents) {
		try {
			console.log(pathname, method);
			if (typeof handlers[pathname][method] === "function")
				handlers[pathname][method](res, contents);
			
			else
				console.log("router error");
		} catch(e) {
			console.log("router catch error!");
			console.log(e);
		}
	}
	
	return route;
})();
