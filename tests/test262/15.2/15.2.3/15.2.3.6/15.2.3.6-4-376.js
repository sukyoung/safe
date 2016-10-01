  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : 2010,
      writable : false,
      enumerable : false,
      configurable : false
    });
    var propertyDefineCorrect = obj.hasOwnProperty("prop");
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    for(var p in obj)
    {
      if (p === "prop")
      {
        return false;
      }
    }
    return propertyDefineCorrect && desc.enumerable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  