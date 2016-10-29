  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      delete srcArr[2];
      delete srcArr[4];
      if (val > 0)
        return true;
      else
        return false;
    }
    Array.prototype[4] = 5;
    var srcArr = [1, 2, 3, 4, 5, ];
    var resArr = srcArr.filter(callbackfn);
    delete Array.prototype[4];
    if (resArr.length === 4 && resArr[0] === 1 && resArr[3] == 5)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  