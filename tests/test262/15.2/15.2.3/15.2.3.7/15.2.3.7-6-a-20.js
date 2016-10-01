  function testcase() 
  {
    try
{      Object.defineProperty(JSON, "prop", {
        value : 11,
        writable : true,
        configurable : true
      });
      var hasProperty = JSON.hasOwnProperty("prop") && JSON.prop === 11;
      Object.defineProperties(JSON, {
        prop : {
          value : 12
        }
      });
      return hasProperty && JSON.prop === 12;}
    finally
{      delete JSON.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  