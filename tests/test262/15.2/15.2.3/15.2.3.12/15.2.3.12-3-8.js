  function testcase() 
  {
    var b = Object.isFrozen(String);
    if (b === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  