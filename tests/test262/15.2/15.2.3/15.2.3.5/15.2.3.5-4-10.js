//  TODO getter/setter
//  function testcase() 
//  {
//    var result = false;
//    Object.defineProperty(Math, "prop", {
//      get : (function () 
//      {
//        result = (this === Math);
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
//      }, Math);
//      return result && newObj.hasOwnProperty("prop");}
//    finally
//{      delete Math.prop;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
