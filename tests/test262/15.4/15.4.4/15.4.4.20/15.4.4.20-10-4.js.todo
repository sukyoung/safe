  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      callCnt++;
    }
    var srcArr = [1, 2, 3, 4, 5, ];
    srcArr["i"] = 10;
    srcArr[true] = 11;
    var resArr = srcArr.filter(callbackfn);
    if (callCnt == 5)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  