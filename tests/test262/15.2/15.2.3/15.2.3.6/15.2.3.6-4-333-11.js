  function testcase() 
  {
    var obj = (function (x) 
    {
      return arguments;
    })(1001);
    Object.defineProperty(obj, "0", {
      value : 2010,
      writable : true,
      enumerable : true,
      configurable : false
    });
    var verifyValue = (obj[0] === 2010);
    return verifyValue;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  