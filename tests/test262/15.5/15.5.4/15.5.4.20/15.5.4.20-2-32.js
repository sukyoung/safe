  function testcase() 
  {
    return String.prototype.trim.call("123#$%abc") === "123#$%abc";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  