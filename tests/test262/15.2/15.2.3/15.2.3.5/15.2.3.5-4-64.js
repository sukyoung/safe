  function testcase() 
  {
    var accessed = false;
    try
{      Math.enumerable = true;
      var newObj = Object.create({
        
      }, {
        prop : Math
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
{      delete Math.enumerable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  