  function testcase() 
  {
    try
{      Object.defineProperty(Date.prototype, "prop", {
        value : 1001,
        writable : true,
        enumerable : true,
        configurable : true
      });
      var dateObj = new Date();
      dateObj.prop = 1002;
      return dateObj.hasOwnProperty("prop") && dateObj.prop === 1002;}
    finally
{      delete Date.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  