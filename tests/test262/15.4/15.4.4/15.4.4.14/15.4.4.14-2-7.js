// XXX
//  function testcase() 
//  {
//    var objOne = {
//      1 : true
//    };
//    var objTwo = {
//      2 : true
//    };
//    Object.defineProperty(objOne, "length", {
//      get : (function () 
//      {
//        return 2;
//      }),
//      configurable : true
//    });
//    Object.defineProperty(objTwo, "length", {
//      get : (function () 
//      {
//        return 2;
//      }),
//      configurable : true
//    });
//    return Array.prototype.indexOf.call(objOne, true) === 1 && Array.prototype.indexOf.call(objTwo, true) === - 1;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
