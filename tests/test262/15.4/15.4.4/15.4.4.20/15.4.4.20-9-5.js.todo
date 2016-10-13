  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      srcArr[1000] = 3;
      return true;
    }
    var srcArr = new Array(10);
    srcArr[1] = 1;
    srcArr[2] = 2;
    var resArr = srcArr.filter(callbackfn);
    if (resArr.length === 2)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  