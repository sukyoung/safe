  function testcase() 
  {
    var obj = {
      
    };
    try
{      @Global.writable = true;
      Object.defineProperty(obj, "property", @Global);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete @Global.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
