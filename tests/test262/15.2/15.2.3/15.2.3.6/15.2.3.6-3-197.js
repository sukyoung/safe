  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "property", {
      writable : new RegExp()
    });
    var beforeWrite = obj.hasOwnProperty("property") && typeof obj.property === "undefined";
    obj.property = "isWritable";
    var afterWrite = (obj.property === "isWritable");
    return beforeWrite && afterWrite;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  