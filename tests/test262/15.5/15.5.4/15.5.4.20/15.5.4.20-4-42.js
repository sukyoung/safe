  function testcase() 
  {
    if ("ab\uFEFFc".trim() === "ab\uFEFFc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  