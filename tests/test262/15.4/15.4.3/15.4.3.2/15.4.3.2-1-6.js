  function testcase() 
  {
    return ! Array.isArray(new String("hello\nworld\\!"));
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  