  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return this.valueOf() === 5;
    }
    var obj = {
      0 : 11,
      length : 2
    };
    var testResult = Array.prototype.map.call(obj, callbackfn, 5);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  