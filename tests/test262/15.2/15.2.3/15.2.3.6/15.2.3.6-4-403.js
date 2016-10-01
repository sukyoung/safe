  function testcase() 
  {
    try
{      Object.defineProperty(Array.prototype, "prop", {
        value : 1001,
        writable : true,
        enumerable : true,
        configurable : true
      });
      var arrObj = [];
      arrObj.prop = 1002;
      return arrObj.hasOwnProperty("prop") && arrObj.prop === 1002;}
    finally
{      delete Array.prototype.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  