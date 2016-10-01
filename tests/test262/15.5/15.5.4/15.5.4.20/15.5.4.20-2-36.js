  function testcase() 
  {
    return (String.prototype.trim.call(new Boolean(false)) === "false");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  