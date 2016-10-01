  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Object, "freeze");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Object.freeze;
    try
{      Object.freeze = "2010";
      var isWritable = (Object.freeze === "2010");
      var isEnumerable = false;
      for(var prop in Object)
      {
        if (prop === "freeze")
        {
          isEnumerable = true;
        }
      }
      delete Object.freeze;
      var isConfigurable = ! Object.hasOwnProperty("freeze");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Object, "freeze", {
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
  