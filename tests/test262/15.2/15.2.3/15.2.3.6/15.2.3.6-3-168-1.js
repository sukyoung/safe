  function testcase() 
  {
    var obj = {
      
    };
    try
{      Boolean.prototype.writable = true;
      var boolObj = new Boolean(true);
      Object.defineProperty(obj, "property", boolObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Boolean.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  