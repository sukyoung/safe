  var __valueOf = String.prototype.valueOf;
  {
    var __result1 = typeof __valueOf !== "function";
    var __expect1 = false;
  }
  try
{    var x = __valueOf();
    $FAIL('#2: "__valueOf = String.prototype.valueOf; var x = __valueOf()" lead to throwing exception');}
  catch (e)
{    {
      var __result2 = ! (e instanceof TypeError);
      var __expect2 = false;
    }}

  