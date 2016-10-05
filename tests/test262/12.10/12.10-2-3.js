  function testcase() 
  {
    var o = "str";
    var foo = 1;
    try
{      with (o)
      {
        foo = 42;
      }}
    catch (e)
{      }

    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  