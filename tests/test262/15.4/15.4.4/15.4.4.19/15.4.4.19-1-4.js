  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof Boolean;
    }
    var obj = new Boolean(true);
    obj.length = 2;
    obj[0] = 11;
    obj[1] = 12;
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[0] === true && testResult[1] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  