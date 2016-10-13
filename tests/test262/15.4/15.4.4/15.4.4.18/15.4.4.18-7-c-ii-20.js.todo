  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (10 === this.threshold);
    }
    var thisArg = {
      threshold : 10
    };
    var obj = {
      0 : 11,
      length : 1
    };
    Array.prototype.forEach.call(obj, callbackfn, thisArg);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  