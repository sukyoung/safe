//  TODO getter/setter
//  function testcase() 
//  {
//    var obj = {
//      
//    };
//    var fun = (function () 
//    {
//      return "ownDataProperty";
//    });
//    Object.defineProperty(obj, "property", {
//      get : fun,
//      configurable : true
//    });
//    var desc = Object.getOwnPropertyDescriptor(obj, "property");
//    var propDefined = "get" in desc;
//    try
//{      delete desc.get;
//      var propDeleted = "get" in desc;
//      return propDefined && ! propDeleted;}
//    catch (e)
//{      return false;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
