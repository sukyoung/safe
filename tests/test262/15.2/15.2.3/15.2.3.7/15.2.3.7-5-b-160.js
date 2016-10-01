  function testcase() 
  {
    var obj = {
      
    };
    var descObj = new Error();
    descObj.writable = false;
    Object.defineProperties(obj, {
      property : descObj
    });
    obj.property = "isWritable";
    return obj.hasOwnProperty("property") && typeof (obj.property) === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  