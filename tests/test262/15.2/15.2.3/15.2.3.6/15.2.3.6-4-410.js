  function testcase() 
  {
    try
{      Object.defineProperty(Object.prototype, "prop", {
        value : 1001,
        writable : false,
        enumerable : false,
        configurable : true
      });
      JSON.prop = 1002;
      return ! JSON.hasOwnProperty("prop") && JSON.prop === 1001;}
    finally
{      delete Object.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  