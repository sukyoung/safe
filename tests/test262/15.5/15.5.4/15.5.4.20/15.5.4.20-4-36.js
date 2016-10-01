  function testcase() 
  {
    if ("ab\u000Bc".trim() === "ab\u000Bc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  