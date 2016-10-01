  function testcase() 
  {
    var obj = {
      
    };
    try
{      JSON.value = "JSON";
      Object.defineProperty(obj, "property", JSON);
      return obj.property === "JSON";}
    finally
{      delete JSON.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  