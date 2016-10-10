var __str = String(.12345);
{
  var __result1 = typeof __str !== "string";
  var __expect1 = false;
}
{
  var __result2 = __str !== "0.12345";
  var __expect2 = false;
}
__str = String(.012345);
{
  var __result3 = typeof __str !== "string";
  var __expect3 = false;
}
{
  var __result4 = __str !== "0.012345";
  var __expect4 = false;
}
__str = String(.0012345);
{
  var __result5 = typeof __str !== "string";
  var __expect5 = false;
}
{
  var __result6 = __str !== "0.0012345";
  var __expect6 = false;
}
__str = String(.00000012345);
{
  var __result7 = typeof __str !== "string";
  var __expect7 = false;
}
{
  var __result8 = __str !== "1.2345e-7";
  var __expect8 = false;
}
