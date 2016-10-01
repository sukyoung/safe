function testcase() 
{
  var strObj = new String("abc");
  strObj.foo = 10;
  var preCheck = Object.isExtensible(strObj);
  Object.seal(strObj);
  delete strObj.foo;
  return preCheck && strObj.foo === 10;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

