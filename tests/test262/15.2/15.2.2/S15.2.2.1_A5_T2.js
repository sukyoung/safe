var num = NaN;
var n_obj = new Object(num);
{
  var __result1 = n_obj.constructor !== Number;
  var __expect1 = false;
}
{
  var __result2 = typeof n_obj !== 'object';
  var __expect2 = false;
}
{
  var __result3 = typeof num;
  var __expect3 = 'number';
}
