  function testcase() 
  {
    var accessed = false;
    try
{      @Global.enumerable = true;
      var newObj = Object.create({
        
      }, {
        prop : @Global
      });
      for(var property in newObj)
      {
        if (property === "prop")
        {
          accessed = true;
        }
      }
      return accessed;}
    finally
{      delete @Global.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
