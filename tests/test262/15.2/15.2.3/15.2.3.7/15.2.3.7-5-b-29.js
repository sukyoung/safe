  function testcase() 
  {
    var obj = {
      
    };
    var arg;
    var accessed = false;
    (function fun() 
    {
      arg = arguments;
    })();
    arg.enumerable = true;
    Object.defineProperties(obj, {
      prop : arg
    });
    for(var property in obj)
    {
      if (property === "prop")
      {
        accessed = true;
      }
    }
    return accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  