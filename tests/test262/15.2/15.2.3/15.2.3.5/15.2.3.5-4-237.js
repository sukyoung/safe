//  TODO getter/setter
//  function testcase() 
//  {
//    var descObj = {
//      
//    };
//    Object.defineProperty(descObj, "get", {
//      get : (function () 
//      {
//        return (function () 
//        {
//          return "ownAccessorProperty";
//        });
//      })
//    });
//    var newObj = Object.create({
//      
//    }, {
//      prop : descObj
//    });
//    return newObj.prop === "ownAccessorProperty";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
