// XXX
//  function testcase() 
//  {
//    var arr = {
//      length : 2
//    };
//    Object.defineProperty(arr, "0", {
//      get : (function () 
//      {
//        Object.defineProperty(Object.prototype, "1", {
//          get : (function () 
//          {
//            return 6.99;
//          }),
//          configurable : true
//        });
//        return 0;
//      }),
//      configurable : true
//    });
//    try
//{      return Array.prototype.indexOf.call(arr, 6.99) === 1;}
//    finally
//{      delete Object.prototype[1];}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
