  function testcase() 
  {
    var arg;
    (function fun() 
    {
      arg = arguments;
    })(1, 2, 3);
    Object.preventExtensions(arg);
    return ! Object.isFrozen(arg);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  