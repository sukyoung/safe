  function testcase() 
  {
    var callCnt = 0;
    function callbackfn(val, idx, obj) 
    {
      arr[1000] = 3;
      callCnt++;
    }
    var arr = new Array(10);
    arr[1] = 1;
    arr[2] = 2;
    arr.forEach(callbackfn);
    if (callCnt === 2)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  