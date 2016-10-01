  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "create");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.create;
    try
{      Object.create = "2010";
      var isWritable = (Object.create === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "create")
        {
          isEnumerable = true;
        }
      }
      delete Object.create;
      var isConfigurable = ! Object.hasOwnProperty("create");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "create", {
        value : temp,
        writable : true,
        enumerable : false,
        configurable : true
      });}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  