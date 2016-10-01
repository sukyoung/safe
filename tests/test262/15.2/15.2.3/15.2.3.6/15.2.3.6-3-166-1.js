  function testcase() 
  {
    var obj = {
      
    };
    try
{      Array.prototype.writable = true;
      var arrObj = [1, 2, 3, ];
      Object.defineProperty(obj, "property", arrObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Array.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  