//  TODO [[DefineOwnProperty]] for Array object
//  function testcase() 
//  {
//    var arrObj = [];
//    Object.defineProperty(arrObj, "length", {
//      value : {
//        toString : (function () 
//        {
//          return '2';
//        })
//      }
//    });
//    return arrObj.length === 2;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
