//  TODO getter/setter
//  function testcase() 
//  {
//    var isEnumerable = false;
//    var newObj = Object.create({
//      
//    }, {
//      prop : {
//        set : (function () 
//        {
//          
//        }),
//        get : (function () 
//        {
//          
//        }),
//        configurable : true
//      }
//    });
//    var hasProperty = newObj.hasOwnProperty("prop");
//    for(var p in newObj)
//    {
//      if (p === "prop")
//      {
//        isEnumerable = true;
//      }
//    }
//    return hasProperty && ! isEnumerable;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
