  function testcase() 
  {
    var obj = {
      
    };
    var descObj = new Date();
    descObj.value = "Date";
    Object.defineProperties(obj, {
      property : descObj
    });
    return obj.property === "Date";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  