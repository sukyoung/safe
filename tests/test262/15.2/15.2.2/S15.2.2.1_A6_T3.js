var obj = new Object((null, 2, 3), 1, 2);
{
  var __result1 = obj.constructor !== Number;
  var __expect1 = false;
}
{
  var __result2 = typeof obj !== "object";
  var __expect2 = false;
}
{
  var __result3 = (obj != 3) || (obj === 3);
  var __expect3 = false;
}
