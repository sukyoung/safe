// XXX
//  function testcase() 
//  {
//    var _NaN = NaN;
//    var a = new Array("NaN", undefined, 0, false, null, {
//      toString : (function () 
//      {
//        return NaN;
//      })
//    }, 
//    "false", 
//    _NaN, 
//    NaN);
//    if (a.indexOf(NaN) === - 1)
//    {
//      return true;
//    }
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
