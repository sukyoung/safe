var o2 = "http://www.google.com/a file with spaces.html";
var e2 = "http://www.google.com/a%20file%20with%20spaces.html";

var __result1 = decodeURI(e2);
var __expect1 = o2;

// d. ii. If k + 2 is greater than or equal to strLen, throw a URIError exception.
var __result2;
try { decodeURI('%1'); } catch(e) { __result2 = e; }
var __expect2 = @URIErr;

// d. iii. If the characters at position (k+1) and (k + 2) within string do not represent hexadecimal digits, throw a URIError exception.
var __result3;
try { decodeURI('%1g'); } catch(e) { __result3 = e; }
var __expect3 = @URIErr;

// vii. 2. If n equals 1 or n is greater than 4, throw a URIError exception.
var __result4;
try { decodeURI('%80'); } catch(e) { __result4 = e; }
var __expect4 = @URIErr;

// vii. 2. If n equals 1 or n is greater than 4, throw a URIError exception.
var __result5;
try { decodeURI('%FF'); } catch(e) { __result5 = e; }
var __expect5 = @URIErr;

