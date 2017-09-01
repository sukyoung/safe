  function testcase() 
  {
    function foo() 
    {
      
    }
    var o = {
      
    };
    var bf = foo.bind(o);
    var desc = Object.getOwnPropertyDescriptor(bf, 'length');
    if (desc.value === 0 && desc.enumerable === false && desc.writable === false && desc.configurable == false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  