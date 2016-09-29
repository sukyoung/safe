  {
    var __result1 = (new Number(1000000000000000000000)).toFixed() !== String(1000000000000000000000);
    var __expect1 = false;
  }
  {
    var __result2 = (new Number(1000000000000000000000)).toFixed(0) !== String(1000000000000000000000);
    var __expect2 = false;
  }
  {
    var __result3 = (new Number(1000000000000000000000)).toFixed(1) !== String(1000000000000000000000);
    var __expect3 = false;
  }
  {
    var __result4 = (new Number(1000000000000000000000)).toFixed(1.1) !== String(1000000000000000000000);
    var __expect4 = false;
  }
  {
    var __result5 = (new Number(1000000000000000000000)).toFixed(0.9) !== String(1000000000000000000000);
    var __expect5 = false;
  }
  {
    var __result6 = (new Number(1000000000000000000000)).toFixed("1") !== String(1000000000000000000000);
    var __expect6 = false;
  }
  {
    var __result7 = (new Number(1000000000000000000000)).toFixed("1.1") !== String(1000000000000000000000);
    var __expect7 = false;
  }
  {
    var __result8 = (new Number(1000000000000000000000)).toFixed("0.9") !== String(1000000000000000000000);
    var __expect8 = false;
  }
  {
    var __result9 = (new Number(1000000000000000000000)).toFixed(Number.NaN) !== String(1000000000000000000000);
    var __expect9 = false;
  }
  {
    var __result10 = (new Number(1000000000000000000000)).toFixed("some string") !== String(1000000000000000000000);
    var __expect10 = false;
  }
  try
{    s = (new Number(1000000000000000000000)).toFixed(Number.POSITIVE_INFINITY);
}
  catch (e)
{    {
      var __result11 = ! (e instanceof RangeError);
      var __expect11 = false;
    }}

  
