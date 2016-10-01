  function testcase() 
  {
    var obj = {
      
    };
    var fun = (function () 
    {
      return "ownSetProperty";
    });
    Object.defineProperty(obj, "property", {
      set : fun,
      configurable : true
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "property");
    try
{      desc.set = "overwriteSetProperty";
      return desc.set === "overwriteSetProperty";}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  