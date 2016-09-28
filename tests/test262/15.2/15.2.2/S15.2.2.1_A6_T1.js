var obj = new Object(1, 2, 3);
{
  var __result1 = obj.constructor !== Number;
  var __expect1 = false;
}
{
  var __result2 = typeof obj !== "object";
  var __expect2 = false;
}
{
  var __result3 = (obj != 1) || (obj === 1);
  var __expect3 = false;
}
