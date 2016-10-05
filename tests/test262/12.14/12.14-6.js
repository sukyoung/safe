  function testcase() 
  {
    var o = {
      foo : (function () 
      {
        return 42;
      })
    };
    try
{      throw o;}
    catch (e)
{      var foo = (function () 
      {
        
      });
      if (foo() === undefined)
      {
        return true;
      }}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  