  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "property", {
      writable : @Global
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
  
