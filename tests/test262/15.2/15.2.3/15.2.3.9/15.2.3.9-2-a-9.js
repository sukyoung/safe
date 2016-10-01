function testcase() 
{
  var funObj = (function () 
  {

  });
  funObj.foo = 10;
  Object.freeze(funObj);
  var desc = Object.getOwnPropertyDescriptor(funObj, "foo");
  delete funObj.foo;
  return funObj.foo === 10 && desc.configurable === false && desc.writable === false;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

