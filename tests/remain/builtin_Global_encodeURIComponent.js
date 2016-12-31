var o1 = "http://xyz.com/?a=12&b=55";
var e1 = "http%3A%2F%2Fxyz.com%2F%3Fa%3D12%26b%3D55";

var __result1 = encodeURIComponent(o1);
var __expect1 = e1;

var __result2;
try { encodeURIComponent('\uD800'); } catch(e) { __result2 = e; }
var __expect2 = @URIErr;

var __result3 = encodeURIComponent('\uD7FF');
var __expect3 = "%ED%9F%BF";

var __result4 = encodeURIComponent('\uD800\uDC00');
var __expect4 = "%F0%90%80%80";

var __result5;
try { encodeURIComponent('\uD800\uDBFF'); } catch(e) { __result5 = e; }
var __expect5 = @URIErr;

var __result6;
try { encodeURIComponent('\uDFFF'); } catch(e) { __result6 = e; }
var __expect6 = @URIErr;

