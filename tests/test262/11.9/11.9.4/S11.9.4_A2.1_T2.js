  try
{    x === 1;
    $ERROR('#1.1: x === 1 throw ReferenceError. Actual: ' + (x === 1));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  