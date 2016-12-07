var o1 = "http://xyz.com/?a=12&b=55";
var e1 = "http%3A%2F%2Fxyz.com%2F%3Fa%3D12%26b%3D55";

var __result1 = decodeURIComponent(e1);
var __expect1 = o1;

// d. ii. If k + 2 is greater than or equal to strLen, throw a URIError exception.
var __result2;
try { decodeURIComponent('%1'); } catch(e) { __result2 = e; }
var __expect2 = @URIErr;

// d. iii. If the characters at position (k+1) and (k + 2) within string do not represent hexadecimal digits, throw a URIError exception.
var __result3;
try { decodeURIComponent('%1g'); } catch(e) { __result3 = e; }
var __expect3 = @URIErr;

// vii. 2. If n equals 1 or n is greater than 4, throw a URIError exception.
var __result4;
try { decodeURIComponent('%80'); } catch(e) { __result4 = e; }
var __expect4 = @URIErr;

// vii. 2. If n equals 1 or n is greater than 4, throw a URIError exception.
var __result5;
try { decodeURIComponent('%FF'); } catch(e) { __result5 = e; }
var __expect5 = @URIErr;

