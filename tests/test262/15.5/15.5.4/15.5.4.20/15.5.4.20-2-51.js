  function testcase() 
  {
    var argObj = (function () 
    {
      return arguments;
    })(1, 2, true);
    return String.prototype.trim.call(argObj) === "[object Arguments]";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  