  function testcase() 
  {
    var obj = {
      
    };
    var descObj = new Number(- 9);
    descObj.value = "Number";
    Object.defineProperties(obj, {
      property : descObj
    });
    return obj.property === "Number";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  