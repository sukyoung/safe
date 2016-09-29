function testcase() 
{
  if (Object.getPrototypeOf(Date) === Function.prototype)
  {
    return true;
  }
}

var __result1 = testcase();
var __expect1 = true;
