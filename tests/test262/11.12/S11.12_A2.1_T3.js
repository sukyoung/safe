  try
{    true ? y : false;
    $ERROR('#1.1: true ? y : false throw ReferenceError. Actual: ' + (true ? y : false));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  