  function testcase() 
  {
    var obj = {
      0 : 11,
      length : 2
    };
    function callbackfn(val, idx, o) 
    {
      return obj === o;
    }
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  