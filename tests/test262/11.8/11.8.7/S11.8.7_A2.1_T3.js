  try
{    "MAX_VALUE" in NUMBER;
    $ERROR('#1.1: "MAX_VALUE" in NUMBER throw ReferenceError. Actual: ' + ("MAX_VALUE" in NUMBER));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  