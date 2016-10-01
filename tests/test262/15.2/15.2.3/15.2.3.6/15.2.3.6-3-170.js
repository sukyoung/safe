  function testcase() 
  {
    var obj = {
      
    };
    try
{      Math.writable = true;
      Object.defineProperty(obj, "property", Math);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Math.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  