  function testcase() 
  {
    try
{      JSON.configurable = true;
      var newObj = Object.create({
        
      }, {
        prop : JSON
      });
      var result1 = newObj.hasOwnProperty("prop");
      delete newObj.prop;
      var result2 = newObj.hasOwnProperty("prop");
      return result1 === true && result2 === false;}
    finally
{      delete JSON.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  