  function testcase() 
  {
    var obj = {
      
    };
    var descObj = new Boolean(false);
    descObj.value = "Boolean";
    Object.defineProperties(obj, {
      property : descObj
    });
    return obj.property === "Boolean";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  