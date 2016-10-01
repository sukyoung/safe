  function testcase() 
  {
    var obj = (function () 
    {
      return arguments;
    })();
    Object.defineProperty(obj, "0", {
      value : 2010,
      writable : false,
      enumerable : true,
      configurable : true
    });
    var valueVerify = (obj[0] === 2010);
    obj[0] = 1001;
    return valueVerify && obj[0] === 2010;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  