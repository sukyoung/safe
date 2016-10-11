var d1 = new Date(6.54321);
{
  var __result1 = d1.valueOf() !== 6;
  var __expect1 = false;
}
var d2 = new Date(- 6.54321);
{
  var __result2 = d2.valueOf() !== - 6;
  var __expect2 = false;
}
var d3 = new Date(6.54321e2);
{
  var __result3 = d3.valueOf() !== 654;
  var __expect3 = false;
}
var d4 = new Date(- 6.54321e2);
{
  var __result4 = d4.valueOf() !== - 654;
  var __expect4 = false;
}
var d5 = new Date(0.654321e1);
{
  var __result5 = d5.valueOf() !== 6;
  var __expect5 = false;
}
var d6 = new Date(- 0.654321e1);
{
  var __result6 = d6.valueOf() !== - 6;
  var __expect6 = false;
}
var d7 = new Date(true);
{
  var __result7 = d7.valueOf() !== 1;
  var __expect7 = false;
}
var d8 = new Date(false);
{
  var __result8 = d8.valueOf() !== 0;
  var __expect8 = false;
}
var d9 = new Date(1.23e15);
{
  var __result9 = d9.valueOf() !== 1.23e15;
  var __expect9 = false;
}
var d10 = new Date(- 1.23e15);
{
  var __result10 = d10.valueOf() !== - 1.23e15;
  var __expect10 = false;
}
var d11 = new Date(1.23e-15);
{
  var __result11 = d11.valueOf() !== 0;
  var __expect11 = false;
}
var d12 = new Date(- 1.23e-15);
{
  var __result12 = d12.valueOf() !== - 0;
  var __expect12 = false;
}
