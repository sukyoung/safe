function testcase() 
{
  var obj = {

  };
  Object.defineProperty(obj, "foo", {
    value : 10,
    enumerable : false,
    configurable : true
  });
  var preCheck = Object.isExtensible(obj);
  Object.seal(obj);
  var beforeDeleted = obj.hasOwnProperty("foo");
  delete obj.foo;
  var afterDeleted = obj.hasOwnProperty("foo");
  return preCheck && beforeDeleted && afterDeleted;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}
