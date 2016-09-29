function testcase() 
{
  if (Object.getPrototypeOf(RegExp) === Function.prototype)
  {
    return true;
  }
}

var __result1 = testcase();
var __expect1 = true;
