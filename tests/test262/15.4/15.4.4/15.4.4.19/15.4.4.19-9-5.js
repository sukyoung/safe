  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val > 10;
    }
    var obj = {
      0 : 9,
      1 : 8,
      length : 0
    };
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  