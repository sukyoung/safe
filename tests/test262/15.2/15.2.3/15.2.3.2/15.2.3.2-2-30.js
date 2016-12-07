function testcase() 
{
  var proto = Object.getPrototypeOf(@Global);
  return proto.isPrototypeOf(@Global) === true;
}

var __result1 = testcase();
var __expect1 = true;
