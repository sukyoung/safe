  function testcase() 
  {
    function f(o) 
    {
      function innerf(o) 
      {
        with (o)
        {
          return x;
        }
      }
      return innerf(o);
    }
    if (f({
      x : 42
    }) === 42)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  