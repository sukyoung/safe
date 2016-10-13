  function testcase() 
  {
    var res = false;
    var o = new Object();
    o.res = true;
    function callbackfn(val, idx, obj) 
    {
      return this.res;
    }
    var srcArr = [1, ];
    var resArr = srcArr.filter(callbackfn, o);
    if (resArr.length === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  