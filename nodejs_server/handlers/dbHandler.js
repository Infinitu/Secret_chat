/* dbHandler.js */

var mongodb = require("mongodb"),
	masterServer = new mongodb.Server("localhost", 27017, { auto_reconnect : true, poolSize : 10 }),
	slaveServer  = new mongodb.Server("localhost", 27018, { auto_reconnect : true, poolSize : 10 }),
	masterDb     = new mongodb.Db("secretChat", masterServer, { w: 1 });
	slaveDb      = new mongodb.Db("secretChat", slaveServer, { w: 1 });


exports.insertDb = function (contents, callback) {
	masterDb.open();
	collection = masterDb.collection("members");
    collection.insert(contents, function(err) {
        console.log("insert Data: ", JSON.stringify(contents));
        callback(err);
        masterDb.close();
    });
};
	
exports.findDb = function (where, options, callback) {
	slaveDb.open();
	collection = slaveDb.collection("members");
	collection.find(where, options).toArray(function(err, data) {
		if (typeof data[0] == "undefined")
			err = 1;
		
        console.log("find data:", JSON.stringify(data[0]));
        callback(err, data[0]);
        slaveDb.close();
    });
};

exports.updateDb = function (where, operator, callback) {
	masterDb.open();
	collection = masterDb.collection("members");
    collection.update(where, operator, function(err, data) {
        console.log("update Data: ", data.result);
        callback(err);
        masterDb.close();
    });
};

exports.removeDb = function (where, callback) {
	masterDb.open();
	collection = masterDb.collection("members");
    collection.remove(where, function (err, data) {
        console.log("remove Data: ", data.result);
        callback(err);
        masterDb.close();
    });
};
