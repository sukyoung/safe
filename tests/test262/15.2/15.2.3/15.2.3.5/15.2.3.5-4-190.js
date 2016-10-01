//  TODO getter/setter
//  function testcase() 
//  {
//    var proto = {
//      value : 100
//    };
//    Object.defineProperty(proto, "writable", {
//      set : (function () 
//      {
//        
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
//    var beforeWrite = (newObj.prop === 100);
//    newObj.prop = "isWritable";
//    var afterWrite = (newObj.prop === 100);
//    return beforeWrite === true && afterWrite === true;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
