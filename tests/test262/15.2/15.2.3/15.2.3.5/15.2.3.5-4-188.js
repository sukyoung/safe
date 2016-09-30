//  TODO getter/setter
//  function testcase() 
//  {
//    var descObj = {
//      value : 100
//    };
//    Object.defineProperty(descObj, "writable", {
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
