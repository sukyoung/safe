  function testcase() 
  {
    return (String.prototype.trim.call(new Number(123)) === "123");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  