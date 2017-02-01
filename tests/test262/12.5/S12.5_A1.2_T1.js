var __result1 = true;
  if (! (new Number(1)))
{
    var __result1 = false;
}

var __result2 = true;
  if (! (new Boolean(true)))
{
    var __result2 = false;
}

var __result3 = true;
  if (! (new String("1")))
{
    var __result3 = false;
}

var __result4 = true;
  if (! (new String("A")))
{
    var __result4 = false;
}

var __result5 = true;
  if (! (new Boolean(false)))
{
    var __result5 = false;
}

var __result6 = true;
  if (! (new Number(NaN)))
{
    var __result6 = false;
}

var __result7 = true;
  if (! (new Number(null)))
{
    var __result7 = false;
}

var __result8 = true;
  if (! (new String(undefined)))
{
    var __result8 = false;
}

var __result9 = true;
  if (! (new String("")))
{
    var __result9 = false;
}

var __expect1 = true;
var __expect2 = true;
var __expect3 = true;
var __expect4 = true;
var __expect5 = true;
var __expect6 = true;
var __expect7 = true;
var __expect8 = true;
var __expect9 = true;
