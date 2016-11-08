  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      testResult = (val > 10);
    }
    var obj = {
      0 : 11,
      1 : 9,
      length : 4294967297
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  