var obj = {
  
};
obj.pop = Array.prototype.pop;
obj.length = 2.5;
var pop = obj.pop();
{
  var __result1 = pop !== undefined;
  var __expect1 = false;
}
{
  var __result2 = obj.length !== 1;
  var __expect2 = false;
}
obj.length = new Number(2);
var pop = obj.pop();
{
  var __result3 = pop !== undefined;
  var __expect3 = false;
}
{
  var __result4 = obj.length !== 1;
  var __expect4 = false;
}

