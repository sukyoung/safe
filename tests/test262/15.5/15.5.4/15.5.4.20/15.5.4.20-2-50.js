  function testcase() 
  {
    var errObj = new Error("test");
    return String.prototype.trim.call(errObj) === "Error: test";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  