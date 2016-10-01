var n_obj = new Object(2 * ([].length + {
  q : 1
}["q"]));
{
  var __result1 = n_obj.constructor !== Number;
  var __expect1 = false;
}
{
  var __result2 = typeof n_obj !== 'object';
  var __expect2 = false;
}
{
  var __result3 = n_obj != 2;
  var __expect3 = false;
}
{
  var __result4 = n_obj === 2;
  var __expect4 = false;
}
