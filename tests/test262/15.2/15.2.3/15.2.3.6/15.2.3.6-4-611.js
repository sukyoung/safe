  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Function.prototype, "bind");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Function.prototype.bind;
    try
{      Function.prototype.bind = "2010";
      var isWritable = (Function.prototype.bind === "2010");
      var isEnumerable = false;
      for(var prop in Function.prototype)
      {
        if (prop === "bind")
        {
          isEnumerable = true;
        }
      }
      delete Function.prototype.bind;
      var isConfigurable = ! Function.prototype.hasOwnProperty("bind");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Function.prototype, "bind", {
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
  