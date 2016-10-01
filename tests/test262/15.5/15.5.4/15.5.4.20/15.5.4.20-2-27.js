  function testcase() 
  {
    return String.prototype.trim.call(123.1234567) === "123.1234567";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  