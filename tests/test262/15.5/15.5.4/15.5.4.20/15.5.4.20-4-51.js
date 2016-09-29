  function testcase() 
  {
    if ("\u000Aabc\u000A".trim() === "abc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  