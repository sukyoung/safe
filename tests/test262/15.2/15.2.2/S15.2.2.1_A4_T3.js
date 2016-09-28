var n_obj = new Object((1 === 1) && ! (false));
{
  var __result1 = n_obj.constructor !== Boolean;
  var __expect1 = false;
}
{
  var __result2 = typeof n_obj !== 'object';
  var __expect2 = false;
}
{
  var __result3 = n_obj != true;
  var __expect3 = false;
}
{
  var __result4 = n_obj === true;
  var __expect4 = false;
}
