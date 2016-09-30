//  TODO getter/setter
//  function testcase() 
//  {
//    var data = "data";
//    var proto = {
//      
//    };
//    Object.defineProperty(proto, "set", {
//      get : (function () 
//      {
//        return (function (value) 
//        {
//          data = value;
//        });
//      })
//    });
//    var ConstructFun = (function () 
//    {
//      
//    });
//    ConstructFun.prototype = proto;
//    var child = new ConstructFun();
//    var newObj = Object.create({
//      
//    }, {
//      prop : child
//    });
//    var hasProperty = newObj.hasOwnProperty("prop");
//    newObj.prop = "overrideData";
//    return hasProperty && data === "overrideData";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
