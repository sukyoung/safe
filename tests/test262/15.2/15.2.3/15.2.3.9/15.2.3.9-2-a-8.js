function testcase() 
{
  var strObj = new String("abc");
  strObj.foo = 10;
  Object.freeze(strObj);
  var desc = Object.getOwnPropertyDescriptor(strObj, "foo");
  delete strObj.foo;
  return strObj.foo === 10 && desc.configurable === false && desc.writable === false;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

