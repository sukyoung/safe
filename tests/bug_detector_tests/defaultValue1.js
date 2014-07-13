valueOf = 1;
toString = false;

var o = {};
o.valueOf = null;
o.toString = undefined;

valueOf = function () { }
toString = function () { }
