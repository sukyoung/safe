  function testcase() 
  {
    var date = new Date(Infinity, 1, 70, 0, 0, 0);
    try
{      date.toISOString();}
    catch (ex)
{      return ex instanceof RangeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  