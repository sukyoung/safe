  function testcase() 
  {
    var res = false;
    var a = new Array();
    a.res = true;
    var result;
    function callbackfn(val, idx, obj) 
    {
      result = this.res;
    }
    var arr = [1, ];
    arr.forEach(callbackfn, a);
    if (result === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  