  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
    }
    var obj = {
      0 : 12,
      length : 4294967296
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return ! accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  