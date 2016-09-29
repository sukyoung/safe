function testcase() 
{
  if (Object.getPrototypeOf(Array) === Function.prototype)
  {
    return true;
  }
}

var __result1 = testcase();
var __expect1 = true;
