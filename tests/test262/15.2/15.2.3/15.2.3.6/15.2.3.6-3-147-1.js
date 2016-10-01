  function testcase() 
  {
    var obj = {
      
    };
    try
{      Object.prototype.value = "JSON";
      Object.defineProperty(obj, "property", JSON);
      return obj.property === "JSON";}
    finally
{      delete Object.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  