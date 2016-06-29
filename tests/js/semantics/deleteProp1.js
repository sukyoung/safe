var x = {p:1};

var __result1 = delete x["p"];
var __expect1 = true;

var __result2 = x.p;
var __expect2 = undefined;