var o2 = "http://www.google.com/a file with spaces.html";
var e2 = "http://www.google.com/a%20file%20with%20spaces.html";

var __result1 = encodeURI(o2);
var __expect1 = e2;

var __result2;
try { encodeURI('\uD800'); } catch(e) { __result2 = e; }
var __expect2 = @URIErr;

var __result3 = encodeURI('\uD7FF');
var __expect3 = "%ED%9F%BF";

var __result4 = encodeURI('\uD800\uDC00');
var __expect4 = "%F0%90%80%80";

var __result5;
try { encodeURI('\uD800\uDBFF'); } catch(e) { __result5 = e; }
var __expect5 = @URIErr;

var __result6;
try { encodeURI('\uDFFF'); } catch(e) { __result6 = e; }
var __expect6 = @URIErr;

