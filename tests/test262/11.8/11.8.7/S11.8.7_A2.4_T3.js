  try
{    max_value in (max_value = "MAX_VALUE", Number);
    $ERROR('#1.1: max_value in (max_value = "MAX_VALUE", Number) throw ReferenceError. Actual: ' + (max_value in (max_value = "MAX_VALUE", Number)));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  {
    var __result2 = (NUMBER = Number, "MAX_VALUE") in NUMBER !== true;
    var __expect2 = false;
  }
  