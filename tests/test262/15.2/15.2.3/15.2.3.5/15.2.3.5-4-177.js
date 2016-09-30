  function testcase() 
  {
    try
{      __Global.value = "GlobalValue";
      var newObj = Object.create({
        
      }, {
        prop : __Global
      });
      return newObj.prop === "GlobalValue";}
    finally
{      delete __Global.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
