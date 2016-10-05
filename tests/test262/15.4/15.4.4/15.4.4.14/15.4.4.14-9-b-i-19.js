// XXX
//  function testcase() 
//  {
//    var arr = [];
//    Object.defineProperty(arr, "0", {
//      set : (function () 
//      {
//        
//      }),
//      configurable : true
//    });
//    try
//{      Object.defineProperty(Array.prototype, "0", {
//        get : (function () 
//        {
//          return 2;
//        }),
//        configurable : true
//      });
//      return arr.indexOf(undefined) === 0;}
//    finally
//{      delete Array.prototype[0];}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
