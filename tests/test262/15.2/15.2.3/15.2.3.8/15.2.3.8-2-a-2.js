function testcase() 
{
  var proto = {
    foo : 0
  };
  var ConstructFun = (function () 
  {

  });
  ConstructFun.prototype = proto;
  var child = new ConstructFun();
  Object.defineProperty(child, "foo", {
    value : 10,
    configurable : true
  });
  var preCheck = Object.isExtensible(child);
  Object.seal(child);
  delete child.foo;
  return preCheck && child.foo === 10;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}
