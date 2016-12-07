  function testcase() 
  {
    var obj = {
      
    };
    try
{      @Global.configurable = true;
      Object.defineProperties(obj, {
        prop : @Global
      });
      var result1 = obj.hasOwnProperty("prop");
      delete obj.prop;
      var result2 = obj.hasOwnProperty("prop");
      return result1 === true && result2 === false;}
    finally
{      delete @Global.configurable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
