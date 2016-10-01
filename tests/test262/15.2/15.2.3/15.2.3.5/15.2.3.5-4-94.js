  function testcase() 
  {
    var accessed = false;
    var argObj = (function () 
    {
      return arguments;
    })();
    var newObj = Object.create({
      
    }, {
      prop : {
        enumerable : argObj
      }
    });
    for(var property in newObj)
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
  