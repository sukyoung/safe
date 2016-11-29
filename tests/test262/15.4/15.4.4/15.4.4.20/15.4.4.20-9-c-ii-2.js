  function testcase() 
  {
    var parCnt = 3;
    var bCalled = false;
    function callbackfn(val, idx, obj) 
    {
      bCalled = true;
      if (arguments.length !== 3)
        parCnt = arguments.length;
    }
    var srcArr = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, ];
    var resArr = srcArr.filter(callbackfn);
    if (bCalled === true && parCnt === 3)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  