  function testcase() 
  {
    var accessed = false;
    var descObj = new Error();
    descObj.enumerable = true;
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
    return accessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  