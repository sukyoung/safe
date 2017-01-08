  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      arr.length = 3;
      callCnt++;
    }
    var arr = [1, 2, 3, 4, 5, ];
    arr.forEach(callbackfn);
    if (callCnt === 3)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  