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
    var srcArr = [1, 2, 3, 4, 5, ];
    var resArr = srcArr.map(callbackfn);
    if (resArr.length === 5 && resArr[4] === undefined)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  