  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "forEach");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Array.prototype.forEach;
    try
{      Array.prototype.forEach = "2010";
      var isWritable = (Array.prototype.forEach === "2010");
      var isEnumerable = false;
      for(var prop in Array.prototype)
      {
        if (prop === "forEach")
        {
          isEnumerable = true;
        }
      }
      delete Array.prototype.forEach;
      var isConfigurable = ! Array.prototype.hasOwnProperty("forEach");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Array.prototype, "forEach", {
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
  