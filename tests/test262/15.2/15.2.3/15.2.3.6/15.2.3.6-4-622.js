  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Date, "now");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Date.now;
    try
{      Date.now = "2010";
      var isWritable = (Date.now === "2010");
      var isEnumerable = false;
      for(var prop in Date)
      {
        if (prop === "now")
        {
          isEnumerable = true;
        }
      }
      delete Date.now;
      var isConfigurable = ! Date.hasOwnProperty("now");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Date, "now", {
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
  