  function testcase() 
  {
    var funObj = (function () 
    {
      return arguments;
    });
    return typeof (String.prototype.trim.call(funObj)) === "string";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  