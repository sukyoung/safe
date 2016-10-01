  function testcase() 
  {
    var accessed = false;
    try
{      __Global.enumerable = true;
      var newObj = Object.create({
        
      }, {
        prop : __Global
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
{      delete __Global.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
