  function testcase() 
  {
    var obj = {
      
    };
    var boolObj = new Boolean(true);
    boolObj.value = "Boolean";
    Object.defineProperty(obj, "property", boolObj);
    return obj.property === "Boolean";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  