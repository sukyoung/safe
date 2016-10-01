  function testcase() 
  {
    return String.prototype.trim.call(false) === "false";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  