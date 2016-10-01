  function testcase() 
  {
    return String.prototype.trim.call("AB\n\\cd") === "AB\n\\cd";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  