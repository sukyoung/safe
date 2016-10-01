//  TODO getter/setter
//  function testcase() 
//  {
//    var proto = {
//      
//    };
//    Object.defineProperty(proto, "set", {
//      get : (function () 
//      {
//        return (function () 
//        {
//          
//        });
//      })
//    });
//    var ConstructFun = (function () 
//    {
//      
//    });
//    ConstructFun.prototype = proto;
//    var child = new ConstructFun();
//    Object.defineProperty(child, "set", {
//      set : (function () 
//      {
//        
//      })
//    });
//    var newObj = Object.create({
//      
//    }, {
//      prop : child
//    });
//    var desc = Object.getOwnPropertyDescriptor(newObj, "prop");
//    return newObj.hasOwnProperty("prop") && typeof desc.set === "undefined";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
