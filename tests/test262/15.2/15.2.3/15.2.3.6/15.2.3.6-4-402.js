  function testcase() 
  {
    try
{      Object.defineProperty(String.prototype, "prop", {
        value : 1001,
        writable : true,
        enumerable : true,
        configurable : true
      });
      var strObj = new String();
      return ! strObj.hasOwnProperty("prop") && strObj.prop === 1001;}
    finally
{      delete String.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  