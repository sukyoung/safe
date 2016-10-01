  function testcase() 
  {
    var obj = {
      
    };
    try
{      String.prototype.value = "String";
      var strObj = new String("abc");
      Object.defineProperty(obj, "property", strObj);
      return obj.property === "String";}
    finally
{      delete String.prototype.value;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  