  function testcase() 
  {
    var obj = {
      
    };
    try
{      Object.prototype.value = "Math";
      Object.defineProperty(obj, "property", Math);
      return obj.property === "Math";}
    finally
{      delete Object.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  