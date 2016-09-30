  function testcase() 
  {
    try
{      Object.create(null);
      return true;}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  