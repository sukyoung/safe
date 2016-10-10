  try
{    false || y;
    $ERROR('#1.1: false || y throw ReferenceError. Actual: ' + (false || y));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  