var str = '';
var n_obj = new Object(str);
{
  var __result1 = n_obj.constructor !== String;
  var __expect1 = false;
}
{
  var __result2 = typeof n_obj !== 'object';
  var __expect2 = false;
}
{
  var __result3 = n_obj != str;
  var __expect3 = false;
}
{
  var __result4 = n_obj === str;
  var __expect4 = false;
}
{
  var __result5 = typeof str;
  var __expect5 = 'string';
}
