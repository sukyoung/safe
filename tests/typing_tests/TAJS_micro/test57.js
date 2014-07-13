
  function f1(x) {return x+100;}
  function f2(x) {return x+10000;}

  var ff = f1;

  function f3( ) {
    ff = f2;
  }

  f3( );

var z =  (function indirect( f ) { return f(2); })( ff );

//dumpValue(ff);
var __result1 = ff;  // for SAFE
var __expect1 = f2;  // for SAFE

//dumpValue(z);
//assert(z === 10002.0);
var __result2 = z;  // for SAFE
var __expect2 = 10002;  // for SAFE

