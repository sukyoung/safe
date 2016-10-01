  function testcase() 
  {
    var obj = {
      
    };
    try
{      __Global.writable = true;
      Object.defineProperty(obj, "property", __Global);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete __Global.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
