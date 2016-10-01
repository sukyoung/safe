  function testcase() 
  {
    var obj = {
      
    };
    var func = (function (a, b) 
    {
      arguments.value = "arguments";
      Object.defineProperties(obj, {
        property : arguments
      });
      return obj.property === "arguments";
    });
    return func();
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  