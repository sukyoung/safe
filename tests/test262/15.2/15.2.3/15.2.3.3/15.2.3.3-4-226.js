  function testcase() 
  {
    var obj = {
      "property" : "ownDataProperty"
    };
    var desc = Object.getOwnPropertyDescriptor(obj, "property");
    var propDefined = "value" in desc;
    try
{      delete desc.value;
      var propDeleted = "value" in desc;
      return propDefined && ! propDeleted;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  