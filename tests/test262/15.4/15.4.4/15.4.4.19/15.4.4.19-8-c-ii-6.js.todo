  function testcase() 
  {
    var obj = {
      0 : 11,
      length : 1
    };
    var thisArg = {
      
    };
    function callbackfn() 
    {
      return this === thisArg && arguments[0] === 11 && arguments[1] === 0 && arguments[2] === obj;
    }
    var testResult = Array.prototype.map.call(obj, callbackfn, thisArg);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  