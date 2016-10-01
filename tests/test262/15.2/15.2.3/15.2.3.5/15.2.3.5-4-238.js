//  TODO getter/setter
//  function testcase() 
//  {
//    var proto = {
//      
//    };
//    Object.defineProperty(proto, "get", {
//      get : (function () 
//      {
//        return (function () 
//        {
//          return "inheritedAccessorProperty";
//        });
//      })
//    });
//    var ConstructFun = (function () 
//    {
//      
//    });
//    ConstructFun.prototype = proto;
//    var descObj = new ConstructFun();
//    var newObj = Object.create({
//      
//    }, {
//      prop : descObj
//    });
//    return newObj.prop === "inheritedAccessorProperty";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
