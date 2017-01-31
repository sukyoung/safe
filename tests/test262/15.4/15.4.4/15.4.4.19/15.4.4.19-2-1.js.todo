  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val > 10;
    }
    var obj = {
      0 : 12,
      1 : 11,
      2 : 9,
      length : 2
    };
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult.length === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  