  function testcase() 
  {
    if ("ab\u0020c".trim() === "ab\u0020c")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  