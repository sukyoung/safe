// Object returned thru function

function Error(msg) {
    this.message = msg;
}

function kast(x) {
    var f = new Error(x);
    return f;
}
var grot = kast(34);
var flop = grot.message;

//dumpValue(flop);
//assert(flop == 34);
var __result1 = flop;  // for SAFE
var __expect1 = 34;  // for SAFE
