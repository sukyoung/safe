var obj = Object.prototype;
Object.prototype = (function () {
  return "shifted";
});
{
  var __result1 = Object.prototype !== obj;
  var __expect1 = false;
}
var __result2 = true;
try
{
  Object.prototype();
  __result2 = false;
} catch (e) {
}
var __expect2 = true;
