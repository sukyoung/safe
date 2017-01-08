  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      testResult = (val > 10);
    }
    var obj = {
      1 : 11,
      2 : 9,
      length : "0002.00"
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  