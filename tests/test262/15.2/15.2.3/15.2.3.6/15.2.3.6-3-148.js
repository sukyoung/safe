  function testcase() 
  {
    var obj = {
      
    };
    var errObj = new Error();
    errObj.value = "Error";
    Object.defineProperty(obj, "property", errObj);
    return obj.property === "Error";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  