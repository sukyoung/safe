  var __toString = String.prototype.toString;
  {
    var __result1 = typeof __toString !== "function";
    var __expect1 = false;
  }
  try
{    var x = __toString();
    $FAIL('#2: "__toString = String.prototype.toString; var x = __toString();" lead to throwing exception');}
  catch (e)
{    {
      var __result2 = ! (e instanceof TypeError);
      var __expect2 = false;
    }}

  