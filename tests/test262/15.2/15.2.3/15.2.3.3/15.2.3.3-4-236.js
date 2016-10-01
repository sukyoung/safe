  function testcase() 
  {
    var obj = {
      "property" : "ownDataProperty"
    };
    var desc = Object.getOwnPropertyDescriptor(obj, "property");
    try
{      desc.writable = "overwriteDataProperty";
      return desc.writable === "overwriteDataProperty";}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  