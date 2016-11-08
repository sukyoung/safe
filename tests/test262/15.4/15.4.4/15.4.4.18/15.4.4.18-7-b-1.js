  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      callCnt++;
    }
    var arr = new Array(10);
    arr[1] = undefined;
    arr.forEach(callbackfn);
    if (callCnt === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  