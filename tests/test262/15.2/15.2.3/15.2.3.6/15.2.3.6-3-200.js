  function testcase() 
  {
    var obj = {
      
    };
    var argObj = (function () 
    {
      return arguments;
    })(1, true, "a");
    Object.defineProperty(obj, "property", {
      writable : argObj
    });
    var beforeWrite = obj.hasOwnProperty("property");
    obj.property = "isWritable";
    var afterWrite = (obj.property === "isWritable");
    return beforeWrite === true && afterWrite === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  