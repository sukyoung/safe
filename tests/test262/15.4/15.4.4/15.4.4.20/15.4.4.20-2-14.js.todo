  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return true;
    }
    var obj = {
      0 : 11,
      1 : 12
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr.length === 0 && ! accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  