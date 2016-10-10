  try
{    x ? true : false;
    $ERROR('#1.1: x ? true : false throw ReferenceError. Actual: ' + (x ? true : false));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  