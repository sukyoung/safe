  try
{    var x = 1;
    var z = (x /= y);
    $ERROR('#1.1: var x = 1; x /= y throw ReferenceError. Actual: ' + (z));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  