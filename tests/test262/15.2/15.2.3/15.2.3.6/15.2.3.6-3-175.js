  function testcase() 
  {
    var obj = {
      
    };
    var argObj = (function () 
    {
      return arguments;
    })();
    argObj.writable = true;
    Object.defineProperty(obj, "property", argObj);
    var beforeWrite = obj.hasOwnProperty("property");
    obj.property = "isWritable";
    var afterWrite = (obj.property === "isWritable");
    return beforeWrite === true && afterWrite === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  