//  TODO getter/setter
//  function testcase() 
//  {
//    var proto = {
//      
//    };
//    proto.prop = {
//      value : 12
//    };
//    var ConstructFun = (function () 
//    {
//      
//    });
//    ConstructFun.prototype = proto;
//    var child = new ConstructFun();
//    Object.defineProperty(child, "prop", {
//      get : (function () 
//      {
//        return {
//          value : 9
//        };
//      }),
//      enumerable : true
//    });
//    var newObj = Object.create({
//      
//    }, child);
//    return newObj.hasOwnProperty("prop") && newObj.prop === 9;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
