  function testcase() 
  {
    function foo() 
    {
      
    }
    var o = {
      
    };
    var bf = foo.bind(o);
    if (Object.getPrototypeOf(bf) === Function.prototype)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  