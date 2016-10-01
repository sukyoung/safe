  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      property : {
        writable : 123
      }
    });
    obj.property = "isWritable";
    return obj.property === "isWritable";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  