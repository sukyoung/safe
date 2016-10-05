// XXX
//  function testcase() 
//  {
//    var arr = [0, 1, 2, "last", ];
//    Object.defineProperty(arr, "0", {
//      get : (function () 
//      {
//        arr.length = 3;
//        return 0;
//      }),
//      configurable : true
//    });
//    return - 1 === arr.indexOf("last");
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
