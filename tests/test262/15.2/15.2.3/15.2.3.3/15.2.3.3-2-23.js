function testcase() 
{
  var obj = {
    "1e-7" : 1
  };
  var desc = Object.getOwnPropertyDescriptor(obj, 0.0000001);
  return desc.value === 1;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

