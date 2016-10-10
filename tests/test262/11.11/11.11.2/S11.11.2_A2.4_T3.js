  try
{    x || (x = true);
    $ERROR('#1.1: x || (x = true) throw ReferenceError. Actual: ' + (x || (x = true)));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  {
    var __result2 = ((y = true) || y) !== true;
    var __expect2 = false;
  }
  