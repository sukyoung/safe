  function testcase() 
  {
    var accessed = false;
    var newObj = Object.create({
      
    }, {
      prop : {
        enumerable : ""
      }
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
  