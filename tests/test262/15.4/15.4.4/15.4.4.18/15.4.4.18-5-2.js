  function testcase() 
  {
    var res = false;
    var o = new Object();
    o.res = true;
    var result;
    function callbackfn(val, idx, obj) 
    {
      result = this.res;
    }
    var arr = [1, ];
    arr.forEach(callbackfn, o);
    if (result === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  