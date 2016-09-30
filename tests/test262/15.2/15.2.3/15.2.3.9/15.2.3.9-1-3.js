  function testcase() 
  {
    var result = false;
    try
{      Object.freeze(false);
      return false;}
    catch (e)
{      result = e instanceof TypeError;}

    try
{      Object.freeze(true);
      return false;}
    catch (e)
{      return result && e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  