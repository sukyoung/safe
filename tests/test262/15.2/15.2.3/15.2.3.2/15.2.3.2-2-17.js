function testcase() 
{
  if (Object.getPrototypeOf(URIError) === Function.prototype)
  {
    return true;
  }
}

var __result1 = testcase();
var __expect1 = true;
