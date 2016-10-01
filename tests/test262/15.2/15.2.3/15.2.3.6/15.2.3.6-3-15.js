  function testcase() 
  {
    var obj = {
      
    };
    try
{      Object.defineProperty(obj, "property", undefined);
      return false;}
    catch (e)
{      return e instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  