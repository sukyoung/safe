  function testcase() 
  {
    try
{      Object.defineProperty({
        
      }, "property", true);
      return false;}
    catch (e)
{      return e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  