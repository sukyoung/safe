  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (obj.length === 2);
    }
    var obj = {
      0 : 12,
      1 : 11,
      2 : 9,
      length : 2
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  