  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = obj instanceof Function;
    }
    var obj = (function (a, b) 
    {
      return a + b;
    });
    obj[0] = 11;
    obj[1] = 9;
    Array.prototype.forEach.call(obj, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  