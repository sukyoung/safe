  try
{    false ? true : z;
    $ERROR('#1.1: false ? true : z throw ReferenceError. Actual: ' + (false ? true : z));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  