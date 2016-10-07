__str = String(1000000000000000000000);
{
  var __result1 = typeof __str !== "string";
  var __expect1 = false;
}
{
  var __result2 = __str !== "1e+21";
  var __expect2 = false;
}
__str = String(10000000000000000000000);
{
  var __result3 = typeof __str !== "string";
  var __expect3 = false;
}
{
  var __result4 = __str !== "1e+22";
  var __expect4 = false;
}
