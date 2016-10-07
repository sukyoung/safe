  function testcase() 
  {
    try
{      null = 42;}
    catch (e)
{      if (e instanceof ReferenceError)
      {
        return true;
      }}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  