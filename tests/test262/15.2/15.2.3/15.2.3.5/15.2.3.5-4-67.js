  function testcase() 
  {
    var accessed = false;
    try
{      JSON.enumerable = true;
      var newObj = Object.create({
        
      }, {
        prop : JSON
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
{      delete JSON.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  