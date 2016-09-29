  {
    var __result1 = (new Number("a")).toFixed() !== "NaN";
    var __expect1 = false;
  }
  {
    var __result2 = (new Number("a")).toFixed(0) !== "NaN";
    var __expect2 = false;
  }
  {
    var __result3 = (new Number("a")).toFixed(1) !== "NaN";
    var __expect3 = false;
  }
  {
    var __result4 = (new Number("a")).toFixed(1.1) !== "NaN";
    var __expect4 = false;
  }
  {
    var __result5 = (new Number("a")).toFixed(0.9) !== "NaN";
    var __expect5 = false;
  }
  {
    var __result6 = (new Number("a")).toFixed("1") !== "NaN";
    var __expect6 = false;
  }
  {
    var __result7 = (new Number("a")).toFixed("1.1") !== "NaN";
    var __expect7 = false;
  }
  {
    var __result8 = (new Number("a")).toFixed("0.9") !== "NaN";
    var __expect8 = false;
  }
  {
    var __result9 = (new Number("a")).toFixed(Number.NaN) !== "NaN";
    var __expect9 = false;
  }
  {
    var __result10 = (new Number("a")).toFixed("some string") !== "NaN";
    var __expect10 = false;
  }
  try
{    s = (new Number("a")).toFixed(Number.POSITIVE_INFINITY);
}
  catch (e)
{    {
      var __result11 = ! (e instanceof RangeError);
      var __expect11 = false;
    }}

  
