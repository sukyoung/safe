  function testcase() 
  {
    var obj = {
      
    };
    try
{      Error.prototype.writable = true;
      var errObj = new Error();
      Object.defineProperty(obj, "property", errObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Error.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  