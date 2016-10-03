//  TODO [[DefineOwnProperty]] for Array object
//  var x = [];
//  x.length = 4294967295;
//  var push = x.push();
//  {
//    var __result1 = push !== 4294967295;
//    var __expect1 = false;
//  }
//  try
//{    x.push("x");
//    $ERROR('#2.1: x = []; x.length = 4294967295; x.push("x") throw RangeError. Actual: ' + (push));}
//  catch (e)
//{    {
//      var __result2 = (e instanceof RangeError) !== true;
//      var __expect2 = false;
//    }}
//
//  {
//    var __result3 = x[4294967295] !== "x";
//    var __expect3 = false;
//  }
//  {
//    var __result4 = x.length !== 4294967295;
//    var __expect4 = false;
//  }
//  
