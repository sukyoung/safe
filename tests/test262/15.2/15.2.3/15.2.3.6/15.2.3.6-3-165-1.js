  function testcase() 
  {
    var obj = {
      
    };
    try
{      Function.prototype.writable = true;
      var funObj = (function (a, b) 
      {
        return a + b;
      });
      Object.defineProperty(obj, "property", funObj);
      var beforeWrite = obj.hasOwnProperty("property");
      obj.property = "isWritable";
      var afterWrite = (obj.property === "isWritable");
      return beforeWrite === true && afterWrite === true;}
    finally
{      delete Function.prototype.writable;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  