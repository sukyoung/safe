  function testcase() 
  {
    var s = String("hello world");
    if (s[NaN] === undefined)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  