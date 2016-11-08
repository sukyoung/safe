  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      srcArr[1000] = 3;
      callCnt++;
      return val;
    }
    var srcArr = new Array(10);
    srcArr[1] = 1;
    srcArr[2] = 2;
    var resArr = srcArr.map(callbackfn);
    if (resArr.length === 10 && callCnt === 2)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  