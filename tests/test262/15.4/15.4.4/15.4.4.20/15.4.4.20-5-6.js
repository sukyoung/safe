  function testcase() 
  {
    var res = false;
    function callbackfn(val, idx, obj) 
    {
      return this.res;
    }
    function foo() 
    {
      
    }
    foo.res = true;
    var srcArr = [1, ];
    var resArr = srcArr.filter(callbackfn, foo);
    if (resArr.length === 1)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  