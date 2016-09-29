  {
    var __result1 = Number.NaN.toFixed() !== "NaN";
    var __expect1 = false;
  }
  {
    var __result2 = Number.NaN.toFixed(0) !== "NaN";
    var __expect2 = false;
  }
  {
    var __result3 = Number.NaN.toFixed(1) !== "NaN";
    var __expect3 = false;
  }
  {
    var __result4 = Number.NaN.toFixed(1.1) !== "NaN";
    var __expect4 = false;
  }
  {
    var __result5 = Number.NaN.toFixed(0.9) !== "NaN";
    var __expect5 = false;
  }
  {
    var __result6 = Number.NaN.toFixed("1") !== "NaN";
    var __expect6 = false;
  }
  {
    var __result7 = Number.NaN.toFixed("1.1") !== "NaN";
    var __expect7 = false;
  }
  {
    var __result8 = Number.NaN.toFixed("0.9") !== "NaN";
    var __expect8 = false;
  }
  {
    var __result9 = Number.NaN.toFixed(Number.NaN) !== "NaN";
    var __expect9 = false;
  }
  {
    var __result10 = Number.NaN.toFixed("some string") !== "NaN";
    var __expect10 = false;
  }
  try
{    s = Number.NaN.toFixed(Number.POSITIVE_INFINITY);
}
  catch (e)
{    {
      var __result11 = ! (e instanceof RangeError);
      var __expect11 = false;
    }}

  
