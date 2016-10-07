  try
{    1 ^ y;
    $ERROR('#1.1: 1 ^ y throw ReferenceError. Actual: ' + (1 ^ y));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  