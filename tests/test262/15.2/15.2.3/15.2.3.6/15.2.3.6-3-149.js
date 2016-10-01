  function testcase() 
  {
    var obj = {
      
    };
    var argObj = (function () 
    {
      return arguments;
    })();
    argObj.value = "arguments";
    Object.defineProperty(obj, "property", argObj);
    return obj.property === "arguments";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  