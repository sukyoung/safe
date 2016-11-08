  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      srcArr.length = 2;
      return true;
    }
    var srcArr = [1, 2, 3, 4, 6, ];
    var resArr = srcArr.filter(callbackfn);
    if (resArr.length === 2)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  