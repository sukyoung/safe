var x = {
  
};
x.concat = Array.prototype.concat;
var y = new Object();
var z = new Array(1, 2);
var arr = x.concat(y, z, - 1, true, "NaN");
arr.getClass = Object.prototype.toString;
{
  var __result1 = arr.getClass() !== "[object " + "Array" + "]";
  var __expect1 = false;
}
{
  var __result2 = arr[0] !== x;
  var __expect2 = false;
}
{
  var __result3 = arr[1] !== y;
  var __expect3 = false;
}
{
  var __result4 = arr[2] !== 1;
  var __expect4 = false;
}
{
  var __result5 = arr[3] !== 2;
  var __expect5 = false;
}
{
  var __result6 = arr[4] !== - 1;
  var __expect6 = false;
}
{
  var __result7 = arr[5] !== true;
  var __expect7 = false;
}
{
  var __result8 = arr[6] !== "NaN";
  var __expect8 = false;
}
// TODO [[DefineOnwProperty]] for Array object
//{
//  var __result9 = arr.length !== 7;
//  var __expect9 = false;
//}
//
