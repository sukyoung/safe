//  TODO getter/setter
//  function testcase() 
//  {
//    var accessed = false;
//    var proto = {
//      enumerable : true
//    };
//    var ConstructFun = (function () 
//    {
//      
//    });
//    ConstructFun.prototype = proto;
//    var descObj = new ConstructFun();
//    Object.defineProperty(descObj, "enumerable", {
//      get : (function () 
//      {
//        return false;
//      })
//    });
//    var newObj = Object.create({
//      
//    }, {
//      prop : descObj
//    });
//    for(var property in newObj)
//    {
//      if (property === "prop")
//      {
//        accessed = true;
//      }
//    }
//    return ! accessed;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
