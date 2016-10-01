  function testcase() 
  {
    var obj = {
      
    };
    try
{      Error.prototype.value = "Error";
      var errObj = new Error();
      Object.defineProperty(obj, "property", errObj);
      return obj.property === "Error";}
    finally
{      delete Error.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  