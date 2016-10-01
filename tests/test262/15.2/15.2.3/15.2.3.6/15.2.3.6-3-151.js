  function testcase() 
  {
    var obj = {
      
    };
    try
{      __Global.value = "global";
      Object.defineProperty(obj, "property", __Global);
      return obj.property === "global";}
    finally
{      delete __Global.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
