// XXX
//  function testcase() 
//  {
//    var preIterVisible = false;
//    var obj = {
//      length : 2
//    };
//    Object.defineProperty(obj, "0", {
//      get : (function () 
//      {
//        preIterVisible = true;
//        return false;
//      }),
//      configurable : true
//    });
//    Object.defineProperty(obj, "1", {
//      get : (function () 
//      {
//        if (preIterVisible)
//        {
//          return true;
//        }
//        else
//        {
//          return false;
//        }
//      }),
//      configurable : true
//    });
//    return Array.prototype.indexOf.call(obj, true) === 1;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
