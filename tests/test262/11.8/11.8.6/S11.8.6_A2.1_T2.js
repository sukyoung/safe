  try
{    object instanceof Object;
    $ERROR('#1.1: object instanceof Object throw ReferenceError. Actual: ' + (object instanceof Object));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  