  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })();
    var preCheck = Object.isExtensible(argObj);
    Object.seal(argObj);
    return preCheck && Object.isSealed(argObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  