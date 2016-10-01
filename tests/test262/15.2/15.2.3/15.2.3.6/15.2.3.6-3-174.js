  function testcase() 
  {
    var obj = {
      
    };
    var errObj = new Error();
    errObj.writable = true;
    Object.defineProperty(obj, "property", errObj);
    var beforeWrite = obj.hasOwnProperty("property");
    obj.property = "isWritable";
    var afterWrite = (obj.property === "isWritable");
    return beforeWrite === true && afterWrite === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  