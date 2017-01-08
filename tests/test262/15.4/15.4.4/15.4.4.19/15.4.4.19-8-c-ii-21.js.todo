  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        return val === 11;
      }
      if (idx === 1)
      {
        return val === 12;
      }
      return false;
    }
    var obj = {
      0 : 11,
      1 : 12,
      length : 2
    };
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[0] === true && testResult[1] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  