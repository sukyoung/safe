  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })();
    argObj.foo = 10;
    var preCheck = Object.isExtensible(argObj);
    Object.seal(argObj);
    delete argObj.foo;
    return preCheck && argObj.foo === 10;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  