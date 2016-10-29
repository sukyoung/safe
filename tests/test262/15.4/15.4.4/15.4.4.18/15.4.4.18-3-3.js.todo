  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
    }
    var obj = {
      0 : 1,
      1 : 1,
      length : 0
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return ! accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  