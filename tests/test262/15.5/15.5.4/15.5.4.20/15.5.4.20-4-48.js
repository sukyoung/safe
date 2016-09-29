  function testcase() 
  {
    if ("abc\u000D".trim() === "abc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  