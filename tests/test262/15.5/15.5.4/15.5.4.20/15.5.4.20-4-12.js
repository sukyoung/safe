  function testcase() 
  {
    if ("abc\u000B".trim() === "abc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  