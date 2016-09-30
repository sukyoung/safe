  function testcase() 
  {
    var arg;
    (function fun() 
    {
      arg = arguments;
    })(1, 2, 3);
    return ! Array.isArray(arg);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  