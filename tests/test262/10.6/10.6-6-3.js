  function testcase() 
  {
    var arguments = undefined;
    return (function () 
    {
      return arguments.length !== undefined;
    })();
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  