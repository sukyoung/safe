  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      property : {
        value : "abc"
      }
    });
    obj.property = "isWritable";
    return obj.property === "abc";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  