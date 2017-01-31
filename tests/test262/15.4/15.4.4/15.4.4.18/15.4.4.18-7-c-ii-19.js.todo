  function testcase() 
  {
    var accessed = false;
    var result = true;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      if (val === 8)
      {
        result = false;
      }
    }
    var obj = {
      0 : 11,
      10 : 12,
      non_index_property : 8,
      length : 20
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return result && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  