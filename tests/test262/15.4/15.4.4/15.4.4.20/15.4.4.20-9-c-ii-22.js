  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      if (val === 11)
      {
        return idx === 0;
      }
      if (val === 12)
      {
        return idx === 1;
      }
      return false;
    }
    var obj = {
      0 : 11,
      1 : 12,
      length : 2
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr.length === 2 && newArr[0] === 11 && newArr[1] === 12;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  