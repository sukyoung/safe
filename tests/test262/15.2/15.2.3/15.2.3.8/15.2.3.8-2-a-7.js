function testcase() 
{
  var funObj = (function () 
  {

  });
  funObj.foo = 10;
  var preCheck = Object.isExtensible(funObj);
  Object.seal(funObj);
  delete funObj.foo;
  return preCheck && funObj.foo === 10;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

