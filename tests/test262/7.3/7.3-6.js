  function testcase() 
  {
    var prop = "66\u2029123";
    return prop === "66\u2029123" && prop[2] === "\u2029" && prop.length === 6;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  