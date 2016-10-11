var x = 9007199254740994.0; /* 2^53 + 2 */
var y = 1.0 - 1 / 65536.0;
var z = x + y;
var d = z - x;
{
  var __result1 = d !== 0;
  var __expect1 = false;
}
