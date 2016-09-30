  function testcase() 
  {
    var accessed = false;
    var descObj = {
      enumerable : false
    };
    var newObj = Object.create({
      
    }, {
      prop : descObj
    });
    for(var property in newObj)
    {
      if (property === "prop")
      {
        accessed = true;
      }
    }
    return ! accessed && newObj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  