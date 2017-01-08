  function testcase() 
  {
    var res = false;
    var result;
    function callbackfn(val, idx, obj) 
    {
      result = this.res;
    }
    function foo() 
    {
      
    }
    foo.res = true;
    var arr = [1, ];
    arr.forEach(callbackfn, foo);
    if (result === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  