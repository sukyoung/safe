  function testcase() 
  {
    var res = false;
    var a = new Array();
    a.res = true;
    function callbackfn(val, idx, obj) 
    {
      return this.res;
    }
    var srcArr = [1, ];
    var resArr = srcArr.filter(callbackfn, a);
    if (resArr.length === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  