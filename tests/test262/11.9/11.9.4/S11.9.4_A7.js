{
  var __result1 = new Object() === new Object();
  var __expect1 = false;
}
{
  var __result2 = new Object(true) === new Object(true);
  var __expect2 = false;
}
{
  var __result3 = new Object(false) === new Object(false);
  var __expect3 = false;
}
{
  var __result4 = new Object(+ 0) === new Object(- 0);
  var __expect4 = false;
}
x = {
};
y = x;
{
  var __result5 = ! (x === y);
  var __expect5 = false;
}
{
  var __result6 = new Boolean(true) === new Number(1);
  var __expect6 = false;
}
{
  var __result7 = new Number(1) === new String("1");
  var __expect7 = false;
}
{
  var __result8 = new String("1") === new Boolean(true);
  var __expect8 = false;
}
