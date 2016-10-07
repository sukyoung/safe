  try
{    1 != y;
    $ERROR('#1: 1 != y throw ReferenceError');}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  