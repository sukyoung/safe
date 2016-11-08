  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return val > 10;
    }
    var obj = {
      0 : 11,
      1 : 12,
      length : 0
    };
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult.length === 0 && ! accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  