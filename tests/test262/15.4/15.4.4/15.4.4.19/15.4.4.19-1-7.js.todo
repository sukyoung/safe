  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof String;
    }
    var testResult = Array.prototype.map.call("abc", callbackfn);
    return testResult[0] === true && testResult[1] === true && testResult[2] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  