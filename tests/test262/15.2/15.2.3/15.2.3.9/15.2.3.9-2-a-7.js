  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })();
    argObj.foo = 10;
    Object.freeze(argObj);
    var desc = Object.getOwnPropertyDescriptor(argObj, "foo");
    delete argObj.foo;
    return argObj.foo === 10 && desc.configurable === false && desc.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  