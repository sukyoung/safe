  function testcase() 
  {
    if ("ab\u200Bc".trim() === "ab\u200Bc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  