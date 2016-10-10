var obj = {
  
};
obj.shift = Array.prototype.shift;
obj[0] = "x";
obj[1] = "y";
obj.length = - 4294967294;
var shift = obj.shift();
{
  var __result1 = shift !== "x";
  var __expect1 = false;
}
{
  var __result2 = obj.length !== 1;
  var __expect2 = false;
}
{
  var __result3 = obj[0] !== "y";
  var __expect3 = false;
}
{
  var __result4 = obj[1] !== undefined;
  var __expect4 = false;
}

