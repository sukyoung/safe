//  TODO getter/setter
//  function testcase() 
//  {
//    var data1 = "data";
//    var data2 = "data";
//    var proto = {
//      
//    };
//    proto.set = (function (value) 
//    {
//      data2 = value;
//    });
//    var ConstructFun = (function () 
//    {
//      
//    });
//    ConstructFun.prototype = proto;
//    var child = new ConstructFun();
//    Object.defineProperty(child, "set", {
//      get : (function () 
//      {
//        return (function (value) 
//        {
//          data1 = value;
//        });
//      })
//    });
//    var newObj = Object.create({
//      
//    }, {
//      prop : child
//    });
//    var hasProperty = newObj.hasOwnProperty("prop");
//    newObj.prop = "overrideData";
//    return hasProperty && data1 === "overrideData" && data2 === "data";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
