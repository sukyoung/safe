  function testcase() 
  {
    var result = false;
    var obj = {
      0 : 11,
      1 : 12,
      length : 2
    };
    function callbackfn(val, idx, o) 
    {
      if (idx === 0)
      {
        obj[idx + 1] = 8;
      }
      if (idx === 1)
      {
        result = (val === 8);
      }
    }
    Array.prototype.forEach.call(obj, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  