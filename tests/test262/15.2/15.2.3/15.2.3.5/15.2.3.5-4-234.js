//  TODO getter/setter
//  function testcase() 
//  {
//    var proto = {
//      get : (function () 
//      {
//        return "inheritedDataProperty";
//      })
//    };
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
//    return newObj.prop === "inheritedDataProperty";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
