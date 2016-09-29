  function testcase() 
  {
    var obj = {
      
    };
    var fun = (function () 
    {
      return "ownGetProperty";
    });
    Object.defineProperty(obj, "property", {
      get : fun,
      configurable : true
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "property");
    try
{      desc.get = "overwriteGetProperty";
      return desc.get === "overwriteGetProperty";}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  