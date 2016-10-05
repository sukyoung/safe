// XXX
//  function testcase() 
//  {
//    var arr = [0, 1, 2, ];
//    Object.defineProperty(arr, "2", {
//      get : (function () 
//      {
//        return "unconfigurable";
//      }),
//      configurable : false
//    });
//    Object.defineProperty(arr, "1", {
//      get : (function () 
//      {
//        arr.length = 2;
//        return 1;
//      }),
//      configurable : true
//    });
//    return 2 === arr.indexOf("unconfigurable");
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
