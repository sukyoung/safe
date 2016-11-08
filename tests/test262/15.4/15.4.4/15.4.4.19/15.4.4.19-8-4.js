  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      srcArr.length = 2;
      callCnt++;
      return 1;
    }
    var srcArr = [1, 2, 3, 4, 5, ];
    var resArr = srcArr.map(callbackfn);
    if (resArr.length === 5 && callCnt === 2 && resArr[2] === undefined)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  