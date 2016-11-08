  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return val > 10;
    }
    var obj = {
      0 : 11,
      1 : 12,
      length : 0
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return accessed === false && obj.length === 0 && newArr.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  