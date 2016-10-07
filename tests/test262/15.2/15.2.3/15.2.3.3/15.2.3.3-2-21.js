function testcase() 
{
  var obj = {
    "1e+22" : 1
  };
  var desc = Object.getOwnPropertyDescriptor(obj, 10000000000000000000000);
  return desc.value === 1;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

