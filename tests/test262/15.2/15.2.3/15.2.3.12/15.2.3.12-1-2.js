  function testcase() 
  {
    try
{      Object.isFrozen(null);}
    catch (e)
{      return (e instanceof TypeError);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  