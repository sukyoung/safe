  function testcase() 
  {
    var obj = {
      
    };
    try
{      Boolean.prototype.value = "Boolean";
      var boolObj = new Boolean(true);
      Object.defineProperty(obj, "property", boolObj);
      return obj.property === "Boolean";}
    finally
{      delete Boolean.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  