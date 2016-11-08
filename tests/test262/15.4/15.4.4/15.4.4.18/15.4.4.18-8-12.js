  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      callCnt++;
    }
    var arr = [1, 2, 3, 4, 5, ];
    arr["i"] = 10;
    arr[true] = 11;
    arr.forEach(callbackfn);
    if (callCnt == 5)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  