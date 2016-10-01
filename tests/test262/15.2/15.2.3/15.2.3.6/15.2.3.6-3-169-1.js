  function testcase() 
  {
    var obj = {
      
    };
    try
{      Number.prototype.writable = true;
      var numObj = new Number(- 2);
      Object.defineProperty(obj, "property", numObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Number.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  