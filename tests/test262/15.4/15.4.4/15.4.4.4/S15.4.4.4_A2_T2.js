var x = {
  
};
x.concat = Array.prototype.concat;
var arr = x.concat();
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
  var __result3 = arr.length !== 1;
  var __expect3 = false;
}

