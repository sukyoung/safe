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
    var resArr = srcArr.map(callbackfn, foo);
    if (resArr[0] === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  