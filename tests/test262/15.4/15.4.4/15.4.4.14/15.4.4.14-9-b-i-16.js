//  TODO getter/setter
//  function testcase() 
//  {
//    try
//{      Object.defineProperty(Object.prototype, "0", {
//        get : (function () 
//        {
//          return 10;
//        }),
//        configurable : true
//      });
//      Object.defineProperty(Object.prototype, "1", {
//        get : (function () 
//        {
//          return 20;
//        }),
//        configurable : true
//      });
//      Object.defineProperty(Object.prototype, "2", {
//        get : (function () 
//        {
//          return 30;
//        }),
//        configurable : true
//      });
//      return 0 === Array.prototype.indexOf.call({
//        length : 3
//      }, 10) && 1 === Array.prototype.indexOf.call({
//        length : 3
//      }, 20) && 2 === Array.prototype.indexOf.call({
//        length : 3
//      }, 30);}
//    finally
//{      delete Object.prototype[0];
//      delete Object.prototype[1];
//      delete Object.prototype[2];}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
