var obj = new Object(null, 2, 3);
{
  var __result1 = obj.constructor !== Object;
  var __expect1 = false;
}
{
  var __result2 = typeof obj !== "object";
  var __expect2 = false;
}
