  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof Error;
    }
    var obj = new Error();
    obj.length = 1;
    obj[0] = 1;
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  