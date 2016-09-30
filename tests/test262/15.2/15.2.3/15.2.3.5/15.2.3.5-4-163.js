//  TODO getter/setter
//  function testcase() 
//  {
//    var proto = {
//      
//    };
//    Object.defineProperty(proto, "value", {
//      get : (function () 
//      {
//        return "inheritedAccessorProperty";
//      })
//    });
//    var ConstructFun = (function () 
//    {
//      
//    });
//    ConstructFun.prototype = proto;
//    var descObj = new ConstructFun();
//    Object.defineProperty(descObj, "value", {
//      set : (function () 
//      {
//        
//      })
//    });
//    var newObj = Object.create({
//      
//    }, {
//      prop : descObj
//    });
//    return newObj.hasOwnProperty("prop") && typeof (newObj.prop) === "undefined";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
