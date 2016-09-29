  function testcase() 
  {
    return String.prototype.trim.call(123.456) === "123.456";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  