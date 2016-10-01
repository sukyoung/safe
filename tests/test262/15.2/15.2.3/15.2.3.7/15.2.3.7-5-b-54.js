  function testcase() 
  {
    var obj = {
      
    };
    var accessed = false;
    var arg;
    (function fun() 
    {
      arg = arguments;
    })(1, 2, 3);
    Object.defineProperties(obj, {
      prop : {
        enumerable : arg
      }
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
  