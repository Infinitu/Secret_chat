/* dbHandler.js */

var mongodb = require('mongodb'),
	server = new mongodb.Server('localhost', 27017, {});
	db = new mongodb.Db('secretChat', server, {w: 1});
	collection = db.collection("members");
	
exports.insertDb = function (contents, res, callback) {
    db.open(function(err) {
        if (err) throw err;
        
        collection.insert(contents, function(err, data) {
            if (err)
            	msgHandler.sendError("deviceId is already existed");
            
            console.log("insert Data: ", data.result);
            console.log(JSON.stringify(contents));
            db.close(callback(res, contents.accessToken));
        });
    });
};

exports.findDb = function (operator, options, callback) {
	db.open(function(err) {
        if (err) throw err;
        collection.find(operator, options).toArray(function(err, foundData) {
            if (err) throw err;
            
            console.log("data :", foundData[0]);
            db.close();
            callback(null, foundData[0]);
        });
    });
};
