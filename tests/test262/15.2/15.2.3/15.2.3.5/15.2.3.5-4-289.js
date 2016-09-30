//  TODO getter/setter
//  function testcase() 
//  {
//    var argObj = (function () 
//    {
//      return arguments;
//    })();
//    var data = "data";
//    argObj.set = (function (value) 
//    {
//      data = value;
//    });
//    var newobj = Object.create({
//      
//    }, {
//      prop : argObj
//    });
//    var hasProperty = newobj.hasOwnProperty("prop");
//    newobj.prop = "overrideData";
//    return hasProperty && data === "overrideData";
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
