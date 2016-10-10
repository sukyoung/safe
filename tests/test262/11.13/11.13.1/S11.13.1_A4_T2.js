  try
{    x = x;
    $ERROR('#1.1: x = x throw ReferenceError. Actual: ' + (x = x));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  