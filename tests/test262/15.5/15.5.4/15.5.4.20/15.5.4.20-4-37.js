  function testcase() 
  {
    if ("ab\u000Cc".trim() === "ab\u000Cc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  