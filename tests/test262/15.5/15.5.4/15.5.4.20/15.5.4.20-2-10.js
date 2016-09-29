  function testcase() 
  {
    return String.prototype.trim.call(+ Infinity) === "Infinity";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  