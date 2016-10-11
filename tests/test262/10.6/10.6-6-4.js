  function testcase() 
  {
    var arguments = undefined;
    return (function (a, b, c) 
    {
      return arguments.length === 0;
    })();
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  