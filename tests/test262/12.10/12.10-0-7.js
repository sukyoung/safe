  function testcase() 
  {
    var o = {
      foo : 1
    };
    with (o)
    {
      foo = 42;
    }
    try
{      foo;}
    catch (e)
{      return true;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  