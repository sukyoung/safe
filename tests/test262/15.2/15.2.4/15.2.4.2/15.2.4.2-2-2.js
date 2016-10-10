function testcase() 
{
  return Object.prototype.toString.apply(null, []) === "[object Null]";
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

