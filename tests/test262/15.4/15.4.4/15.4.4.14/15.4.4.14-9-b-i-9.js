// XXX
//  function testcase() 
//  {
//    var arr = [, , , ];
//    Object.defineProperty(arr, "0", {
//      get : (function () 
//      {
//        return 0;
//      }),
//      configurable : true
//    });
//    Object.defineProperty(arr, "1", {
//      get : (function () 
//      {
//        return 1;
//      }),
//      configurable : true
//    });
//    Object.defineProperty(arr, "2", {
//      get : (function () 
//      {
//        return 2;
//      }),
//      configurable : true
//    });
//    return arr.indexOf(0) === 0 && arr.indexOf(1) === 1 && arr.indexOf(2) === 2;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
