  function testcase() 
  {
    var result = false;
    var obj = {
      0 : 11,
      length : 1
    };
    var thisArg = {
      
    };
    function callbackfn() 
    {
      result = (this === thisArg && arguments[0] === 11 && arguments[1] === 0 && arguments[2] === obj);
    }
    Array.prototype.forEach.call(obj, callbackfn, thisArg);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  