  function testcase() 
  {
    var obj = {
      
    };
    try
{      String.prototype.writable = true;
      var strObj = new String("abc");
      Object.defineProperty(obj, "property", strObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete String.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  