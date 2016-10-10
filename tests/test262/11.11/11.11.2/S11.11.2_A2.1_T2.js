  try
{    x || true;
    $ERROR('#1.1: x || true throw ReferenceError. Actual: ' + (x || true));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  