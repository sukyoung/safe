  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return true;
    }
    var obj = {
      0 : 12,
      length : 4294967296
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return ! accessed && newArr.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  