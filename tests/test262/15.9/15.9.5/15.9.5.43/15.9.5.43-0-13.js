  function testcase() 
  {
    var timeZoneMinutes = new Date().getTimezoneOffset() * (- 1);
    var date, dateStr;
    try
{      if (timeZoneMinutes > 0)
      {
        date = new Date(1970, 0, 100000001, 0, 0 + timeZoneMinutes + 60, 0, 1);
        dateStr = date.toISOString();
        return false;
      }
      else
      {
        date = new Date(1970, 0, 100000001, 0, 0, 0, 1);
        dateStr = date.toISOString();
        return false;
      }}
    catch (e)
{      return e instanceof RangeError;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  