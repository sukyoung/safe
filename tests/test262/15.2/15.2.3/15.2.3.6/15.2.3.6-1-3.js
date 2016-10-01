  function testcase() 
  {
    try
{      Object.defineProperty(5, "foo", {
        
      });
      return false;}
    catch (e)
{      return e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  