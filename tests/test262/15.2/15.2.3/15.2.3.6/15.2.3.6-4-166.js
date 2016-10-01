//  TODO [[DefineOwnProperty]] for Array object
//  function testcase() 
//  {
//    var arrObj = [0, 1, ];
//    Object.defineProperty(arrObj, "length", {
//      value : 1,
//      writable : true
//    });
//    var indexDeleted = ! arrObj.hasOwnProperty("1");
//    arrObj.length = 10;
//    return indexDeleted && arrObj.length === 10;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
