  function testcase() 
  {
    try
{      Object.getOwnPropertyDescriptor(0, "foo");}
    catch (e)
{      if (e instanceof TypeError)
      {
        return true;
      }}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  