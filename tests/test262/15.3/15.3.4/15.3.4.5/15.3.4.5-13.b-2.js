  function testcase() 
  {
    function foo(x, y) 
    {
      
    }
    var o = {
      
    };
    var bf = foo.bind(o);
    if (bf.length === 2)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  