  try
{    x > (x = 1);
    $ERROR('#1.1: x > (x = 1) throw ReferenceError. Actual: ' + (x > (x = 1)));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  {
    var __result2 = (y = 1) > y !== false;
    var __expect2 = false;
  }
  