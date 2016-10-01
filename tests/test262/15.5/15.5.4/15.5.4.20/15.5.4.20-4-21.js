  function testcase() 
  {
    if ("\u000Cabc\u000C".trim() === "abc")
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  