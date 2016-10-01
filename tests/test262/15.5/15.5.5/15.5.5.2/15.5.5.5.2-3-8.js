  function testcase() 
  {
    var s = String("hello world");
    if (s[Math.pow(2, 32) - 1] === undefined)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  