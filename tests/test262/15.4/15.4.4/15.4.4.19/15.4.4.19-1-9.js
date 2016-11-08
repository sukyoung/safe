  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof Function;
    }
    var obj = (function (a, b) 
    {
      return a + b;
    });
    obj[0] = 11;
    obj[1] = 9;
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult[0] === true && testResult[1] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  