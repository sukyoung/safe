  function testcase() 
  {
    var date = new String("1970-01-00000:00:00.000Z");
    try
{      Date.prototype.toISOString.call(date);
      return false;}
    catch (ex)
{      return ex instanceof TypeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  