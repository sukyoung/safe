  function testcase() 
  {
    if ("ab\u00A0c".trim() === "ab\u00A0c")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  