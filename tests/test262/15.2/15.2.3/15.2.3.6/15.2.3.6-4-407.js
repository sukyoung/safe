  function testcase() 
  {
    try
{      Object.defineProperty(Error.prototype, "prop", {
        value : 1001,
        writable : true,
        enumerable : true,
        configurable : true
      });
      var errObj = new Error();
      return ! errObj.hasOwnProperty("prop") && errObj.prop === 1001;}
    finally
{      delete Error.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  