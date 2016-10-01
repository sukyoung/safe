  function testcase() 
  {
    try
{      Object.defineProperty(Function.prototype, "prop", {
        value : 1001,
        writable : false,
        enumerable : false,
        configurable : true
      });
      var funObj = (function () 
      {
        
      });
      var verifyEnumerable = false;
      for(var p in funObj)
      {
        if (p === "prop")
        {
          verifyEnumerable = true;
        }
      }
      return ! funObj.hasOwnProperty("prop") && ! verifyEnumerable;}
    finally
{      delete Function.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  