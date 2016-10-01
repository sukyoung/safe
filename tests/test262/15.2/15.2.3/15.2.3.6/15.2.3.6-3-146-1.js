  function testcase() 
  {
    var obj = {
      
    };
    try
{      RegExp.prototype.value = "RegExp";
      var regObj = new RegExp();
      Object.defineProperty(obj, "property", regObj);
      return obj.property === "RegExp";}
    finally
{      delete RegExp.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  