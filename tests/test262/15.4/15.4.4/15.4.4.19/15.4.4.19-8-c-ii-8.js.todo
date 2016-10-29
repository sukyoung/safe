  function testcase() 
  {
    var obj = {
      0 : 9,
      1 : 12,
      length : 2
    };
    function callbackfn(val, idx, o) 
    {
      if (idx === 0)
      {
        obj[idx + 1] = 8;
      }
      return val > 10;
    }
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[1] === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  