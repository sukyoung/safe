  function testcase() 
  {
    var obj = {
      
    };
    try
{      JSON.writable = true;
      Object.defineProperty(obj, "property", JSON);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete JSON.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  