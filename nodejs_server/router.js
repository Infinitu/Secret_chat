/* route.js */

var userEventHandler   = require("./handlers/userEventHandler"),
	friendEventHandler = require("./handlers/friendEventHandler"),
	nickNameTagHandler = require("./handlers/nickNameTagHandler");

exports.route = (function() {
	var handlers = { "/join"      : { POST : userEventHandler.join },
					 "/setting"   : { POST : userEventHandler.read, PUT : userEventHandler.update },
					 "/getTag"    : { POST : nickNameTagHandler.getNickNameTag },
					 "/addfriend" : { POST : friendEventHandler.find },
					 "/main"      : { POST : friendEventHandler.read },
					 "/uninstall" : { DELETE : userEventHandler.remove },
					 "/profileImages" : { GET : friendEventHandler.showImage }
	};
	
	function route(res, pathname, method, contents) {
		if (typeof handlers[pathname][method] === "function")
			handlers[pathname][method](res, contents);
		
		else
			console.log("router error");
	}
	
	return route;
})();
