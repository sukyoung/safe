  function testcase() 
  {
    try
{      Date.prototype.toISOString.call(15);
      return false;}
    catch (ex)
{      return ex instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  