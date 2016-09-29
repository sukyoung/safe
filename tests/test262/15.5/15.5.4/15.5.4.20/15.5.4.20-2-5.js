  function testcase() 
  {
    return String.prototype.trim.call(+ 0) === "0";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  