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
    var resArr = srcArr.map(callbackfn, a);
    if (resArr[0] === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  