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
    foo.prototype.res = true;
    var f = new foo();
    var arr = [1, ];
    arr.forEach(callbackfn, f);
    if (result === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  