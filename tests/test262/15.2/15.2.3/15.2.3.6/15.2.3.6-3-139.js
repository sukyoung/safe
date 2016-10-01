  function testcase() 
  {
    var obj = {
      
    };
    var funObj = (function (a, b) 
    {
      return a + b;
    });
    funObj.value = "Function";
    Object.defineProperty(obj, "property", funObj);
    return obj.property === "Function";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  