  function testcase() 
  {
    try
{      JSON.prop = {
        value : 12,
        enumerable : true
      };
      var newObj = Object.create({
        
      }, JSON);
      return newObj.hasOwnProperty("prop");}
    finally
{      delete JSON.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  