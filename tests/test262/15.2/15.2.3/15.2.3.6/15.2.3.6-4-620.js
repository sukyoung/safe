  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Array.prototype, "reduceRight");
    var propertyAreCorrect = (desc.writable === true && desc.enumerable === false && desc.configurable === true);
    var temp = Array.prototype.reduceRight;
    try
{      Array.prototype.reduceRight = "2010";
      var isWritable = (Array.prototype.reduceRight === "2010");
      var isEnumerable = false;
      for(var prop in Array.prototype)
      {
        if (prop === "reduceRight")
        {
          isEnumerable = true;
        }
      }
      delete Array.prototype.reduceRight;
      var isConfigurable = ! Array.prototype.hasOwnProperty("reduceRight");
      return propertyAreCorrect && isWritable && ! isEnumerable && isConfigurable;}
    finally
{      Object.defineProperty(Array.prototype, "reduceRight", {
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
  