  function testcase() 
  {
    var obj = {
      
    };
    try
{      Date.prototype.writable = true;
      dateObj = new Date();
      Object.defineProperty(obj, "property", dateObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Date.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  