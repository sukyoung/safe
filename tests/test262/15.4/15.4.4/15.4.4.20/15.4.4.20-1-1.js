  function testcase() 
  {
    try
{      Array.prototype.filter.call(undefined);
      return false;}
    catch (ex)
{      return ex instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  