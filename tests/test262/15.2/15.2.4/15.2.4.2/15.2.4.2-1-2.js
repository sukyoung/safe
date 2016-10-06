function testcase() 
{
  return Object.prototype.toString.apply(undefined, []) === "[object Undefined]";
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

