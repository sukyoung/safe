// XXX
//  function testcase() 
//  {
//    var arr = {
//      2 : 2,
//      length : 20
//    };
//    Object.defineProperty(arr, "0", {
//      get : (function () 
//      {
//        delete Object.prototype[1];
//        return 0;
//      }),
//      configurable : true
//    });
//    try
//{      Object.prototype[1] = 1;
//      return - 1 === Array.prototype.indexOf.call(arr, 1);}
//    finally
//{      delete Object.prototype[1];}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
