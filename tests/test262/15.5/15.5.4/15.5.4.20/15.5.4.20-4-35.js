  function testcase() 
  {
    if ("ab\u0009c".trim() === "ab\u0009c")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  