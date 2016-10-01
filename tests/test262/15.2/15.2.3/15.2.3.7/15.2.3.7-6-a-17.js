  function testcase() 
  {
    try
{      Object.defineProperty(Math, "prop", {
        value : 11,
        writable : true,
        configurable : true
      });
      var hasProperty = Math.hasOwnProperty("prop") && Math.prop === 11;
      Object.defineProperties(Math, {
        prop : {
          value : 12
        }
      });
      return hasProperty && Math.prop === 12;}
    finally
{      delete Math.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  