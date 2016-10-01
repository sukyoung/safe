  function testcase() 
  {
    return String.prototype.trim.call(NaN) === "NaN";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  