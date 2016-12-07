  function testcase() 
  {
    var obj = {
      
    };
    try
{      @Global.value = "global";
      Object.defineProperty(obj, "property", @Global);
      return obj.property === "global";}
    finally
{      delete @Global.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
