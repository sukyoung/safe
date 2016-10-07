  try
{    x = y;
    $ERROR('#1.1: x = y throw ReferenceError. Actual: ' + (x = y));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  