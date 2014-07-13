var C = function(v) {
  this.inc = function() {return ++v}
}

var x = new C(7);

//dumpValue(x.inc());
//dumpState();
var __result1 = x.inc();  // for SAFE
var __expect1 = 8;  // for SAFE
