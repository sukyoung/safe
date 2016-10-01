  function testcase() 
  {
    try
{      Object.defineProperty(Number.prototype, "prop", {
        value : 1001,
        writable : false,
        enumerable : false,
        configurable : true
      });
      var numObj = new Number();
      numObj.prop = 1002;
      return ! numObj.hasOwnProperty("prop") && numObj.prop === 1001;}
    finally
{      delete Number.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  