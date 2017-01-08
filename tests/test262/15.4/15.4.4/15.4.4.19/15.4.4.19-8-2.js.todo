  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      srcArr[4] = - 1;
      if (val > 0)
        return 1;
      else
        return 0;
    }
    var srcArr = [1, 2, 3, 4, 5, ];
    var resArr = srcArr.map(callbackfn);
    if (resArr.length === 5 && resArr[4] === 0)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  