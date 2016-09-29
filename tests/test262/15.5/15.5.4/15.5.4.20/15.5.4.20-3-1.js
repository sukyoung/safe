  function testcase() 
  {
    var lineTerminatorsStr = "\u000A\u000D\u2028\u2029";
    return (lineTerminatorsStr.trim() === "");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  