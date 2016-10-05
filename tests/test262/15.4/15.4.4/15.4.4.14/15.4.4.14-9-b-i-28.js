// XXX
//  function testcase() 
//  {
//    var preIterVisible = false;
//    var arr = [];
//    Object.defineProperty(arr, "0", {
//      get : (function () 
//      {
//        preIterVisible = true;
//        return false;
//      }),
//      configurable : true
//    });
//    Object.defineProperty(arr, "1", {
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
//    return arr.indexOf(true) === 1;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
