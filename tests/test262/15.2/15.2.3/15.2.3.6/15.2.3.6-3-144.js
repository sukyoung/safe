  function testcase() 
  {
    var obj = {
      
    };
    try
{      Math.value = "Math";
      Object.defineProperty(obj, "property", Math);
      return obj.property === "Math";}
    finally
{      delete Math.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  