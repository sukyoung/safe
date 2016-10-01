  function testcase() 
  {
    var obj = {
      
    };
    try
{      RegExp.prototype.writable = true;
      var regObj = new RegExp();
      Object.defineProperty(obj, "property", regObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete RegExp.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  