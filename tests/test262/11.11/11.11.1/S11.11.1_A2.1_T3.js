  try
{    true && y;
    $ERROR('#1.1: true && y throw ReferenceError. Actual: ' + (true && y));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  