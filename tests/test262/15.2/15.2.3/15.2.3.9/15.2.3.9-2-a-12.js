function testcase() 
{
  var strObj = new String("abc");
  Object.freeze(strObj);
  var desc = Object.getOwnPropertyDescriptor(strObj, "0");
  delete strObj[0];
  return strObj[0] === "a" && desc.configurable === false && desc.writable === false;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

