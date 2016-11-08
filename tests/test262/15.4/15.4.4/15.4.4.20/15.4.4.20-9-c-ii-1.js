  function testcase() 
  {
    var bPar = true;
    var bCalled = false;
    function callbackfn(val, idx, obj) 
    {
      bCalled = true;
      if (obj[idx] !== val)
        bPar = false;
    }
    var srcArr = [0, 1, true, null, new Object(), "five", ];
    srcArr[999999] = - 6.6;
    var resArr = srcArr.filter(callbackfn);
    if (bCalled === true && bPar === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  