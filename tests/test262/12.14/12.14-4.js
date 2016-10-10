  function testcase() 
  {
    var o = {
      foo : 42
    };
    try
{      throw o;}
    catch (e)
{      var foo;
      if (foo === undefined)
      {
        return true;
      }}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  