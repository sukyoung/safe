var obj = {
  
};
obj.pop = Array.prototype.pop;
obj.length = NaN;
var pop = obj.pop();
{
  var __result1 = pop !== undefined;
  var __expect1 = false;
}
{
  var __result2 = obj.length !== 0;
  var __expect2 = false;
}
obj.length = Number.POSITIVE_INFINITY;
var pop = obj.pop();
{
  var __result3 = pop !== undefined;
  var __expect3 = false;
}
{
  var __result4 = obj.length !== 0;
  var __expect4 = false;
}
obj.length = Number.NEGATIVE_INFINITY;
var pop = obj.pop();
{
  var __result5 = pop !== undefined;
  var __expect5 = false;
}
{
  var __result6 = obj.length !== 0;
  var __expect6 = false;
}
obj.length = - 0;
var pop = obj.pop();
{
  var __result7 = pop !== undefined;
  var __expect7 = false;
}
if (obj.length !== 0)
{
  $ERROR('#8: var obj = {}; obj.length = -0; obj.pop = Array.prototype.pop; obj.pop(); obj.length === 0. Actual: ' + (obj.length));
}
else
{
  {
    var __result8 = 1 / obj.length !== Number.POSITIVE_INFINITY;
    var __expect8 = false;
  }
}
obj.length = 0.5;
var pop = obj.pop();
{
  var __result9 = pop !== undefined;
  var __expect9 = false;
}
{
  var __result10 = obj.length !== 0;
  var __expect10 = false;
}
obj.length = new Number(0);
var pop = obj.pop();
{
  var __result11 = pop !== undefined;
  var __expect11 = false;
}
{
  var __result12 = obj.length !== 0;
  var __expect12 = false;
}

