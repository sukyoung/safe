  function testcase() 
  {
    return String.prototype.trim.call("undefined") === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  