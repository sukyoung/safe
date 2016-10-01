  function testcase() 
  {
    var obj = {
      
    };
    var numObj = new Number(- 2);
    numObj.writable = true;
    Object.defineProperty(obj, "property", numObj);
    var beforeWrite = obj.hasOwnProperty("property");
    obj.property = "isWritable";
    var afterWrite = (obj.property === "isWritable");
    return beforeWrite === true && afterWrite === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  