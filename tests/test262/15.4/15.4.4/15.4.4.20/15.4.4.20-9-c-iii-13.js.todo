  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return NaN;
    }
    var newArr = [11, ].filter(callbackfn);
    return newArr.length === 0 && accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  