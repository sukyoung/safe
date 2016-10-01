  function testcase() 
  {
    var obj = {
      
    };
    var descObj = new RegExp();
    descObj.value = "RegExp";
    Object.defineProperties(obj, {
      property : descObj
    });
    return obj.property === "RegExp";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  