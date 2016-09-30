  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })();
    Object.freeze(argObj);
    return Object.isFrozen(argObj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  