  function testcase() 
  {
    var result = false;
    var obj = {
      0 : 11,
      length : 2
    };
    function callbackfn(val, idx, o) 
    {
      result = (obj === o);
    }
    Array.prototype.forEach.call(obj, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  