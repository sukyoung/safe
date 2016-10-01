  function testcase() 
  {
    var obj = {
      
    };
    try
{      Function.prototype.value = "Function";
      var funObj = (function (a, b) 
      {
        return a + b;
      });
      Object.defineProperty(obj, "property", funObj);
      return obj.property === "Function";}
    finally
{      delete Function.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  