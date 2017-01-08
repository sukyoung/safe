  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof String;
    }
    var obj = new String("abc");
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[0] === true && testResult[1] === true && testResult[2] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  