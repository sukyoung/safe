function testcase() 
{
  var proto = Object.getPrototypeOf(__Global);
  return proto.isPrototypeOf(__Global) === true;
}

var __result1 = testcase();
var __expect1 = true;
