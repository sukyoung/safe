  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      callCnt++;
      arr[2] = 3;
      arr[5] = 6;
    }
    var arr = [1, 2, , 4, 5, ];
    arr.forEach(callbackfn);
    if (callCnt === 5)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  