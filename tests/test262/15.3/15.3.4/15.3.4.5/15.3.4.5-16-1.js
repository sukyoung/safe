  function testcase() 
  {
    function foo() 
    {
      
    }
    var o = {
      
    };
    var bf = foo.bind(o);
    var ex = Object.isExtensible(bf);
    if (ex === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  