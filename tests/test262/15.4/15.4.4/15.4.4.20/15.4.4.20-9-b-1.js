  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      callCnt++;
      return false;
    }
    var srcArr = new Array(10);
    srcArr[1] = undefined;
    var resArr = srcArr.filter(callbackfn);
    if (resArr.length === 0 && callCnt === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  