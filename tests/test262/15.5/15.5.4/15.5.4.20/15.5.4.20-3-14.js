  function testcase() 
  {
    return "a\0\u0000bc".trim() === "a\0\u0000bc";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  