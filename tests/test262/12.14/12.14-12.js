  function testcase() 
  {
    function f(o) 
    {
      function innerf(o) 
      {
        try
{          throw o;}
        catch (e)
{          return e.x;}

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
  