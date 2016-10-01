  function testcase() 
  {
    var obj = {
      
    };
    var func = (function (a, b) 
    {
      return a + b;
    });
    func.value = "Function";
    Object.defineProperties(obj, {
      property : func
    });
    return obj.property === "Function";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  