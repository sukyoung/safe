function testcase() 
{
  var obj = {
    "0.00001" : 1
  };
  var desc = Object.getOwnPropertyDescriptor(obj, 0.00001);
  return desc.value === 1;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

