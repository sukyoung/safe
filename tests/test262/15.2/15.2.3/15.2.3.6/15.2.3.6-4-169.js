//  TODO [[DefineOwnProperty] for Array object
//  function testcase() 
//  {
//    var arrObj = [0, 1, 2, ];
//    Object.defineProperty(arrObj, "1", {
//      configurable : false
//    });
//    Object.defineProperty(arrObj, "2", {
//      configurable : true
//    });
//    try
//{      Object.defineProperty(arrObj, "length", {
//        value : 1
//      });
//      return false;}
//    catch (e)
//{      return e instanceof TypeError && arrObj.length === 2 && ! arrObj.hasOwnProperty("2");}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
