  function testcase() 
  {
    var obj = {
      
    };
    var funObj = (function (a, b) 
    {
      return a + b;
    });
    funObj.writable = true;
    Object.defineProperty(obj, "property", funObj);
    var beforeWrite = obj.hasOwnProperty("property");
    obj.property = "isWritable";
    var afterWrite = (obj.property === "isWritable");
    return beforeWrite === true && afterWrite === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  