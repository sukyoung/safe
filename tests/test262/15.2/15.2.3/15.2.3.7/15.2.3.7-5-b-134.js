  function testcase() 
  {
    var obj = {
      
    };
    var descObj = new Error();
    descObj.value = "Error";
    Object.defineProperties(obj, {
      property : descObj
    });
    return obj.property === "Error";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  