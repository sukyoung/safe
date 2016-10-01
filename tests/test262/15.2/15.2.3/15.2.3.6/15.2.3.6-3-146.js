  function testcase() 
  {
    var obj = {
      
    };
    var regObj = new RegExp();
    regObj.value = "RegExp";
    Object.defineProperty(obj, "property", regObj);
    return obj.property === "RegExp";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  