  function testcase() 
  {
    var accessed = false;
    var newObj = Object.create({
      
    }, {
      prop : {
        enumerable : undefined
      }
    });
    for(var property in newObj)
    {
      if (property === "prop")
      {
        accessed = true;
      }
    }
    return ! accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  