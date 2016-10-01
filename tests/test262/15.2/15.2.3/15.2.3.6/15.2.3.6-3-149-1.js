  function testcase() 
  {
    var obj = {
      
    };
    try
{      Object.prototype.value = "arguments";
      var argObj = (function () 
      {
        return arguments;
      })();
      Object.defineProperty(obj, "property", argObj);
      return obj.property === "arguments";}
    finally
{      delete Object.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  