  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      delete srcArr[4];
      if (val > 0)
        return 1;
      else
        return 0;
    }
    Array.prototype[4] = 5;
    var srcArr = [1, 2, 3, 4, 5, ];
    var resArr = srcArr.map(callbackfn);
    delete Array.prototype[4];
    if (resArr.length === 5 && resArr[4] === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  