  function testcase() 
  {
    try
{      @Global.value = "GlobalValue";
      var newObj = Object.create({
        
      }, {
        prop : @Global
      });
      return newObj.prop === "GlobalValue";}
    finally
{      delete @Global.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
