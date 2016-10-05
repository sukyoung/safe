// XXX
//  function testcase() 
//  {
//    try
//{      Object.defineProperty(Array.prototype, "0", {
//        get : (function () 
//        {
//          return 10;
//        }),
//        configurable : true
//      });
//      Object.defineProperty(Array.prototype, "1", {
//        get : (function () 
//        {
//          return 20;
//        }),
//        configurable : true
//      });
//      Object.defineProperty(Array.prototype, "2", {
//        get : (function () 
//        {
//          return 30;
//        }),
//        configurable : true
//      });
//      return 0 === [, , , ].indexOf(10) && 1 === [, , , ].indexOf(20) && 2 === [, , , ].indexOf(30);}
//    finally
//{      delete Array.prototype[0];
//      delete Array.prototype[1];
//      delete Array.prototype[2];}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
