//  TODO getter/setter
//  function testcase() 
//  {
//    var result = false;
//    Object.defineProperty(JSON, "prop", {
//      get : (function () 
//      {
//        result = (this === JSON);
//        return {
//          
//        };
//      }),
//      enumerable : true,
//      configurable : true
//    });
//    try
//{      var newObj = Object.create({
//        
//      }, JSON);
//      return result && newObj.hasOwnProperty("prop");}
//    finally
//{      delete JSON.prop;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
