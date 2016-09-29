  function testcase() 
  {
    var s = new String("hello world");
    s.foo = 1;
    if (s["foo"] === 1)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  