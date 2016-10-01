var obj = {
  
};
obj.pop = Array.prototype.pop;
obj[4294967294] = "x";
obj.length = - 1;
var pop = obj.pop();
{
  var __result1 = pop !== "x";
  var __expect1 = false;
}
{
  var __result2 = obj.length !== 4294967294;
  var __expect2 = false;
}
{
  var __result3 = obj[4294967294] !== undefined;
  var __expect3 = false;
}

