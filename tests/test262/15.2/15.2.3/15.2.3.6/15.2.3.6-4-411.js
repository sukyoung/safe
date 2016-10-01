  function testcase() 
  {
    try
{      Object.defineProperty(Object.prototype, "prop", {
        value : 1001,
        writable : false,
        enumerable : false,
        configurable : true
      });
      var verifyEnumerable = false;
      for(var p in Math)
      {
        if (p === "prop")
        {
          verifyEnumerable = true;
        }
      }
      return ! Math.hasOwnProperty("prop") && ! verifyEnumerable;}
    finally
{      delete Object.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  