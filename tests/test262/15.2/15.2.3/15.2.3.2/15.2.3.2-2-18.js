function testcase() 
{
  if (Object.getPrototypeOf(JSON) === Object.prototype)
  {
    return true;
  }
}

var __result1 = testcase();
var __expect1 = true;
