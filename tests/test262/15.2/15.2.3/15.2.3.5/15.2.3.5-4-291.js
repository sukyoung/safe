//  TODO getter/setter
//  function testcase() 
//  {
//    var data = "data";
//    try
//{      __Global.set = (function (value) 
//      {
//        data = value;
//      });
//      var newObj = Object.create({
//        
//      }, {
//        prop : __Global
//      });
//      var hasProperty = newObj.hasOwnProperty("prop");
//      newObj.prop = "overrideData";
//      return hasProperty && data === "overrideData";}
//    finally
//{      delete __Global.set;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
