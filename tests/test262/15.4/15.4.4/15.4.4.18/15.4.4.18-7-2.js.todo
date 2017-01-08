  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      if (callCnt == 0)
        delete arr[3];
      callCnt++;
    }
    var arr = [1, 2, 3, 4, 5, ];
    arr.forEach(callbackfn);
    if (callCnt === 4)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  