  function testcase() 
  {
    function f(o) 
    {
      var x = 42;
      function innerf(o) 
      {
        try
{          throw o;}
        catch (e)
{          return x;}

      }
      return innerf(o);
    }
    if (f({
      
    }) === 42)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  