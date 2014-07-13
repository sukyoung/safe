Date instanceof Object; // true

var o = new Object();
//if(Math.random()) o = function() {};
Date instanceof o; // TypeError
