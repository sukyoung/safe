  function testcase() 
  {
    var obj = {
      
    };
    try
{      Object.prototype.writable = true;
      var argObj = (function () 
      {
        return arguments;
      })();
      Object.defineProperty(obj, "property", argObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Object.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  